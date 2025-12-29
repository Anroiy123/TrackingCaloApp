package com.example.trackingcaloapp.ui.home;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.data.repository.FoodEntryRepository;
import com.example.trackingcaloapp.data.repository.WorkoutEntryRepository;
import com.example.trackingcaloapp.model.DailyCalorieSum;
import com.example.trackingcaloapp.model.FoodEntryWithFood;
import com.example.trackingcaloapp.model.MacroSum;
import com.example.trackingcaloapp.model.MealTypeCalories;
import com.example.trackingcaloapp.model.WorkoutEntryWithWorkout;
import com.example.trackingcaloapp.ui.main.RecentActivityAdapter;
import com.example.trackingcaloapp.ui.view.OverflowProgressBar;
import com.example.trackingcaloapp.utils.CalorieCalculator;
import com.example.trackingcaloapp.utils.ChartHelper;
import com.example.trackingcaloapp.utils.DateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class HomeFragment extends Fragment {

    // Interface để communicate với MainActivity
    public interface OnNavigationListener {
        void navigateToDiary();
    }

    private OnNavigationListener navigationListener;

    private TextView tvGreeting;
    private TextView tvNetCalories;
    private TextView tvCaloriesConsumed;
    private TextView tvCaloriesBurned;
    private TextView tvCaloriesRemaining;
    private TextView tvGoalInfo;
    private TextView tvProgressPercent;
    private TextView tvCalorieWarning;
    private OverflowProgressBar progressCalories;
    private RecyclerView rvRecentActivities;
    private TextView tvNoActivities;
    private TextView tvViewAll;
    private LinearLayout layoutRecentHeader;
    private LinearLayout layoutRecentContent;
    private ImageView ivExpandCollapse;
    private boolean isRecentActivitiesExpanded = true;

    // Chart views
    private TabLayout tabChartType;
    private ViewFlipper chartFlipper;
    private LineChart chartLine;
    private BarChart chartBar;
    private PieChart chartPie;

    // Period toggle views
    private ChipGroup chipGroupPeriod;
    private TextView tvStatisticsTitle;
    private int selectedPeriod = 7; // Default: 7 days

    private UserPreferences userPreferences;
    private FoodEntryRepository foodEntryRepository;
    private WorkoutEntryRepository workoutEntryRepository;
    private RecentActivityAdapter recentActivityAdapter;

    // Cached values for calculation
    private float cachedConsumed = 0f;
    private float cachedBurned = 0f;
    private List<FoodEntryWithFood> cachedFoodEntries;
    private List<WorkoutEntryWithWorkout> cachedWorkoutEntries;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationListener) {
            navigationListener = (OnNavigationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNavigationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userPreferences = new UserPreferences(requireContext());

        AppDatabase db = AppDatabase.getInstance(requireContext());
        foodEntryRepository = new FoodEntryRepository(db.foodEntryDao());
        workoutEntryRepository = new WorkoutEntryRepository(db.workoutEntryDao());

        initViews(view);
        setupCharts();
        loadTodayData();
        loadChartData();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh goal info khi quay lại từ Profile (user có thể đã thay đổi mục tiêu)
        refreshGoalDisplay();
        loadTodayData();
        loadChartData();
    }
    
    /**
     * Refresh hiển thị mục tiêu calo - gọi khi quay lại từ màn hình khác
     */
    private void refreshGoalDisplay() {
        int calorieGoal = userPreferences.getDailyCalorieGoal();
        tvGoalInfo.setText(getString(R.string.goal_format, calorieGoal));
        
        // Cập nhật lại net calories với goal mới
        updateNetCalories();
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvNetCalories = view.findViewById(R.id.tvNetCalories);
        tvCaloriesConsumed = view.findViewById(R.id.tvCaloriesConsumed);
        tvCaloriesBurned = view.findViewById(R.id.tvCaloriesBurned);
        tvCaloriesRemaining = view.findViewById(R.id.tvCaloriesRemaining);
        tvGoalInfo = view.findViewById(R.id.tvGoalInfo);
        tvProgressPercent = view.findViewById(R.id.tvProgressPercent);
        tvCalorieWarning = view.findViewById(R.id.tvCalorieWarning);
        progressCalories = view.findViewById(R.id.progressCalories);
        rvRecentActivities = view.findViewById(R.id.rvRecentActivities);
        tvNoActivities = view.findViewById(R.id.tvNoActivities);

        // Set greeting with user name
        String userName = userPreferences.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvGreeting.setText("Xin chào, " + userName + "! 👋");
        } else {
            tvGreeting.setText("Xin chào! 👋");
        }

        recentActivityAdapter = new RecentActivityAdapter();
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentActivities.setAdapter(recentActivityAdapter);

        // Recent activities toggle views
        tvViewAll = view.findViewById(R.id.tvViewAll);
        layoutRecentHeader = view.findViewById(R.id.layoutRecentHeader);
        layoutRecentContent = view.findViewById(R.id.layoutRecentContent);
        ivExpandCollapse = view.findViewById(R.id.ivExpandCollapse);

        // Setup click listeners
        setupRecentActivitiesToggle();
        setupViewAllClick();

        // Chart views
        tabChartType = view.findViewById(R.id.tabChartType);
        chartFlipper = view.findViewById(R.id.chartFlipper);
        chartLine = view.findViewById(R.id.chartLine);
        chartBar = view.findViewById(R.id.chartBar);
        chartPie = view.findViewById(R.id.chartPie);

        // Period toggle views
        chipGroupPeriod = view.findViewById(R.id.chipGroupPeriod);
        tvStatisticsTitle = view.findViewById(R.id.tvStatisticsTitle);
    }

    private void setupCharts() {
        // Setup chart configurations
        ChartHelper.setupLineChart(chartLine, requireContext());
        ChartHelper.setupBarChart(chartBar, requireContext());
        ChartHelper.setupPieChart(chartPie, requireContext());

        // Setup tab listener to switch between charts
        tabChartType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                chartFlipper.setDisplayedChild(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Setup period toggle listener
        setupPeriodToggle();
    }

    private void setupPeriodToggle() {
        chipGroupPeriod.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip1Day) {
                selectedPeriod = 1;
            } else if (checkedId == R.id.chip7Day) {
                selectedPeriod = 7;
            } else if (checkedId == R.id.chip30Day) {
                selectedPeriod = 30;
            }
            
            updateStatisticsTitle();
            loadChartData();
        });
    }

    private void setupRecentActivitiesToggle() {
        layoutRecentHeader.setOnClickListener(v -> {
            isRecentActivitiesExpanded = !isRecentActivitiesExpanded;
            
            // Animate icon rotation
            float rotation = isRecentActivitiesExpanded ? 0f : -180f;
            ObjectAnimator animator = ObjectAnimator.ofFloat(ivExpandCollapse, "rotation", rotation);
            animator.setDuration(200);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
            
            // Toggle content visibility with animation
            if (isRecentActivitiesExpanded) {
                layoutRecentContent.setVisibility(View.VISIBLE);
                layoutRecentContent.setAlpha(0f);
                layoutRecentContent.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();
            } else {
                layoutRecentContent.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> layoutRecentContent.setVisibility(View.GONE))
                    .start();
            }
        });
    }

    private void setupViewAllClick() {
        tvViewAll.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.navigateToDiary();
            }
        });
    }

    private void updateStatisticsTitle() {
        tvStatisticsTitle.setText("Thống kê");
    }

    private void loadChartData() {
        // Get date range based on selected period
        long[] dateRange = ChartHelper.getDateRangeForPeriod(selectedPeriod);
        long startDate = dateRange[0];
        long endDate = dateRange[1];

        // Load LineChart data - Daily calorie trend
        foodEntryRepository.getDailyCaloriesSummary(startDate, endDate)
            .observe(getViewLifecycleOwner(), dailyData -> {
                ChartHelper.updateLineChartData(chartLine, dailyData, requireContext());
            });

        // Load BarChart data - Meal type comparison
        foodEntryRepository.getCaloriesByMealType(startDate, endDate)
            .observe(getViewLifecycleOwner(), mealData -> {
                ChartHelper.updateBarChartData(chartBar, mealData, requireContext());
            });

        // Load PieChart data - Macro distribution
        foodEntryRepository.getMacroSummary(startDate, endDate)
            .observe(getViewLifecycleOwner(), macroData -> {
                ChartHelper.updatePieChartData(chartPie, macroData, requireContext());
            });
    }

    private void loadTodayData() {
        long startOfDay = DateUtils.getStartOfDay(System.currentTimeMillis());
        long endOfDay = DateUtils.getEndOfDay(System.currentTimeMillis());
        
        // Cập nhật hiển thị goal info (đọc mới mỗi lần load)
        int calorieGoal = userPreferences.getDailyCalorieGoal();
        tvGoalInfo.setText(getString(R.string.goal_format, calorieGoal));

        LiveData<Float> consumedLiveData = foodEntryRepository.getTotalCaloriesByDate(startOfDay, endOfDay);
        consumedLiveData.observe(getViewLifecycleOwner(), consumed -> {
            cachedConsumed = consumed != null ? consumed : 0f;
            tvCaloriesConsumed.setText(String.valueOf((int) cachedConsumed));
            updateNetCalories(); // Không truyền calorieGoal, đọc trực tiếp trong method
        });

        LiveData<Float> burnedLiveData = workoutEntryRepository.getTotalCaloriesBurnedByDate(startOfDay, endOfDay);
        burnedLiveData.observe(getViewLifecycleOwner(), burned -> {
            cachedBurned = burned != null ? burned : 0f;
            tvCaloriesBurned.setText(String.valueOf((int) cachedBurned));
            updateNetCalories(); // Không truyền calorieGoal, đọc trực tiếp trong method
        });

        LiveData<List<FoodEntryWithFood>> foodEntriesLiveData = foodEntryRepository.getEntriesWithFoodByDate(startOfDay, endOfDay);
        foodEntriesLiveData.observe(getViewLifecycleOwner(), foodEntries -> {
            updateRecentActivities(foodEntries, null);
        });

        LiveData<List<WorkoutEntryWithWorkout>> workoutEntriesLiveData = workoutEntryRepository.getEntriesWithWorkoutByDate(startOfDay, endOfDay);
        workoutEntriesLiveData.observe(getViewLifecycleOwner(), workoutEntries -> {
            updateRecentActivities(null, workoutEntries);
        });
    }
    
    /**
     * Cập nhật net calories, remaining và progress bar.
     * Đọc calorieGoal trực tiếp từ UserPreferences để đảm bảo luôn có giá trị mới nhất.
     */
    private void updateNetCalories() {
        // Đọc calorieGoal trực tiếp để đảm bảo giá trị mới nhất sau khi user thay đổi trong Profile
        int calorieGoal = userPreferences.getDailyCalorieGoal();
        
        float netCalories = cachedConsumed - cachedBurned;
        float remaining = calorieGoal - netCalories;

        tvNetCalories.setText(String.valueOf((int) netCalories));
        tvCaloriesRemaining.setText(String.valueOf((int) remaining));

        // Calculate actual progress (không cap)
        float progress = calorieGoal > 0 ? (netCalories / calorieGoal) * 100 : 0;
        
        // Update progress bar với giá trị thực
        progressCalories.setProgress(progress);
        
        // Update percent text với màu sắc
        updateProgressText(progress);
        
        // Update calorie warning message
        updateCalorieWarning(remaining);
    }

    /**
     * Cập nhật hiển thị cảnh báo khi vượt mức calo cho phép
     * @param remaining Số calo còn lại (âm nếu vượt mức)
     */
    private void updateCalorieWarning(float remaining) {
        if (tvCalorieWarning == null) return;
        
        if (remaining < 0) {
            int overAmount = (int) Math.abs(remaining);
            String warningText = getString(R.string.calorie_warning_format, overAmount);
            tvCalorieWarning.setText(warningText);
            tvCalorieWarning.setVisibility(View.VISIBLE);
        } else {
            tvCalorieWarning.setVisibility(View.GONE);
        }
    }

    /**
     * Cập nhật text hiển thị phần trăm tiến độ và đổi màu khi vượt mục tiêu
     * @param progress Phần trăm tiến độ (0-200+)
     */
    private void updateProgressText(float progress) {
        if (tvProgressPercent == null) return;
        
        // Hiển thị text: cap tại 200%+
        if (progress > 200) {
            tvProgressPercent.setText("200%+");
        } else {
            tvProgressPercent.setText((int) progress + "%");
        }
        
        // Đổi màu text khi vượt mục tiêu (>100%)
        int textColor = progress > 100 
            ? ContextCompat.getColor(requireContext(), R.color.error)
            : ContextCompat.getColor(requireContext(), R.color.primary);
        tvProgressPercent.setTextColor(textColor);
    }

    private void updateRecentActivities(List<FoodEntryWithFood> foodEntries, List<WorkoutEntryWithWorkout> workoutEntries) {
        if (foodEntries != null) {
            cachedFoodEntries = foodEntries;
        }
        if (workoutEntries != null) {
            cachedWorkoutEntries = workoutEntries;
        }

        boolean hasActivities = (cachedFoodEntries != null && !cachedFoodEntries.isEmpty()) ||
                               (cachedWorkoutEntries != null && !cachedWorkoutEntries.isEmpty());

        if (hasActivities) {
            tvNoActivities.setVisibility(View.GONE);
            rvRecentActivities.setVisibility(View.VISIBLE);
            recentActivityAdapter.setData(cachedFoodEntries, cachedWorkoutEntries);
        } else {
            tvNoActivities.setVisibility(View.VISIBLE);
            rvRecentActivities.setVisibility(View.GONE);
        }
    }
}
