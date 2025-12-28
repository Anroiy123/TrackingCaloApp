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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private TextView tvDate;
    private TextView tvNetCalories;
    private TextView tvCaloriesConsumed;
    private TextView tvCaloriesBurned;
    private TextView tvCaloriesRemaining;
    private TextView tvGoalInfo;
    private TextView tvTargetInfo;
    private TextView tvProgressPercent;
    private ProgressBar progressCalories;
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
        loadTodayData();
        loadChartData();
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvDate = view.findViewById(R.id.tvDate);
        tvNetCalories = view.findViewById(R.id.tvNetCalories);
        tvCaloriesConsumed = view.findViewById(R.id.tvCaloriesConsumed);
        tvCaloriesBurned = view.findViewById(R.id.tvCaloriesBurned);
        tvCaloriesRemaining = view.findViewById(R.id.tvCaloriesRemaining);
        tvGoalInfo = view.findViewById(R.id.tvGoalInfo);
        tvTargetInfo = view.findViewById(R.id.tvTargetInfo);
        tvProgressPercent = view.findViewById(R.id.tvProgressPercent);
        progressCalories = view.findViewById(R.id.progressCalories);
        rvRecentActivities = view.findViewById(R.id.rvRecentActivities);
        tvNoActivities = view.findViewById(R.id.tvNoActivities);

        tvDate.setText(DateUtils.getDisplayDate(System.currentTimeMillis()));

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
        int calorieGoal = userPreferences.getDailyCalorieGoal();

        tvGoalInfo.setText(getString(R.string.goal_format, calorieGoal));
        
        // Hiển thị thông tin target weight nếu có
        updateTargetWeightInfo();

        LiveData<Float> consumedLiveData = foodEntryRepository.getTotalCaloriesByDate(startOfDay, endOfDay);
        consumedLiveData.observe(getViewLifecycleOwner(), consumed -> {
            cachedConsumed = consumed != null ? consumed : 0f;
            tvCaloriesConsumed.setText(String.valueOf((int) cachedConsumed));
            updateNetCalories(calorieGoal);
        });

        LiveData<Float> burnedLiveData = workoutEntryRepository.getTotalCaloriesBurnedByDate(startOfDay, endOfDay);
        burnedLiveData.observe(getViewLifecycleOwner(), burned -> {
            cachedBurned = burned != null ? burned : 0f;
            tvCaloriesBurned.setText(String.valueOf((int) cachedBurned));
            updateNetCalories(calorieGoal);
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
    
    private void updateTargetWeightInfo() {
        if (tvTargetInfo == null) return;
        
        if (userPreferences.hasTargetWeight() && userPreferences.hasTargetDate()) {
            float currentWeight = userPreferences.getUserWeight();
            float targetWeight = userPreferences.getTargetWeight();
            long targetDate = userPreferences.getTargetDate();
            float weeklyRate = userPreferences.getWeeklyRate();
            
            // Tính số ngày còn lại
            long now = System.currentTimeMillis();
            long daysRemaining = (targetDate - now) / (24L * 60L * 60L * 1000L);
            
            if (daysRemaining > 0) {
                boolean isLosing = currentWeight > targetWeight;
                String direction = isLosing ? "Giảm" : "Tăng";
                float weightDiff = Math.abs(currentWeight - targetWeight);
                
                // Format: "Mục tiêu: 65kg • Còn 30 ngày • Giảm 0.5kg/tuần"
                String targetInfo = String.format("🎯 %.1fkg • Còn %d ngày • %s %.2fkg/tuần",
                        targetWeight, daysRemaining, direction, weeklyRate);
                tvTargetInfo.setText(targetInfo);
                tvTargetInfo.setVisibility(View.VISIBLE);
            } else {
                // Đã quá hạn
                tvTargetInfo.setText("⏰ Đã đến hạn mục tiêu - Hãy cập nhật!");
                tvTargetInfo.setVisibility(View.VISIBLE);
            }
        } else {
            tvTargetInfo.setVisibility(View.GONE);
        }
    }

    private void updateNetCalories(int calorieGoal) {
        float netCalories = cachedConsumed - cachedBurned;
        float remaining = calorieGoal - netCalories;

        tvNetCalories.setText(String.valueOf((int) netCalories));
        tvCaloriesRemaining.setText(String.valueOf((int) remaining));

        int progress = calorieGoal > 0 ? (int) ((netCalories / calorieGoal) * 100) : 0;
        progress = Math.min(progress, 100);
        progressCalories.setProgress(progress);
        
        // Cập nhật phần trăm tiến độ
        if (tvProgressPercent != null) {
            tvProgressPercent.setText(progress + "%");
        }
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
