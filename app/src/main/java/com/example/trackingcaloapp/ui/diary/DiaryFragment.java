package com.example.trackingcaloapp.ui.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.core.content.ContextCompat;
import com.example.trackingcaloapp.ui.view.OverflowProgressBar;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.data.repository.FoodEntryRepository;
import com.example.trackingcaloapp.data.repository.WorkoutEntryRepository;
import com.example.trackingcaloapp.model.MacroSum;
import com.example.trackingcaloapp.utils.ChartHelper;
import com.example.trackingcaloapp.utils.DateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;

public class DiaryFragment extends Fragment {

    private MaterialButton btnPrevDay, btnNextDay;
    private TextView tvSelectedDate;
    private TextView tvTotalConsumed, tvTotalBurned, tvNetCalories;
    private TextView tvCalorieGoal, tvCaloriesRemaining, tvProgressPercent;
    private OverflowProgressBar progressCalories;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    // Macro progress views
    private OverflowProgressBar progressProtein, progressCarbs, progressFat;
    private TextView tvProteinPercent, tvCarbsPercent, tvFatPercent;
    private TextView tvProteinValue, tvCarbsValue, tvFatValue;

    // Chart views
    private TabLayout tabChartType;
    private ViewFlipper chartFlipper;
    private BarChart chartBar;
    private PieChart chartPie;
    private LineChart chartLine;

    private UserPreferences userPreferences;
    private FoodEntryRepository foodEntryRepository;
    private WorkoutEntryRepository workoutEntryRepository;

    private long selectedDate = System.currentTimeMillis();
    private DiaryFragmentPagerAdapter pagerAdapter;
    
    // Cached values
    private float cachedConsumed = 0f;
    private float cachedBurned = 0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userPreferences = new UserPreferences(requireContext());
        AppDatabase db = AppDatabase.getInstance(requireContext());
        foodEntryRepository = new FoodEntryRepository(db.foodEntryDao());
        workoutEntryRepository = new WorkoutEntryRepository(db.workoutEntryDao());

        initViews(view);
        setupDateNavigation();
        setupCharts();
        setupViewPager();
        loadData();
    }


    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews(View view) {
        btnPrevDay = view.findViewById(R.id.btnPrevDay);
        btnNextDay = view.findViewById(R.id.btnNextDay);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvTotalConsumed = view.findViewById(R.id.tvTotalConsumed);
        tvTotalBurned = view.findViewById(R.id.tvTotalBurned);
        tvNetCalories = view.findViewById(R.id.tvNetCalories);
        tvCalorieGoal = view.findViewById(R.id.tvCalorieGoal);
        tvCaloriesRemaining = view.findViewById(R.id.tvCaloriesRemaining);
        tvProgressPercent = view.findViewById(R.id.tvProgressPercent);
        progressCalories = view.findViewById(R.id.progressCalories);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Chart views
        tabChartType = view.findViewById(R.id.tabChartType);
        chartFlipper = view.findViewById(R.id.chartFlipper);
        chartBar = view.findViewById(R.id.chartBar);
        chartPie = view.findViewById(R.id.chartPie);
        chartLine = view.findViewById(R.id.chartLine);

        // Macro progress views
        progressProtein = view.findViewById(R.id.progressProtein);
        progressCarbs = view.findViewById(R.id.progressCarbs);
        progressFat = view.findViewById(R.id.progressFat);
        tvProteinPercent = view.findViewById(R.id.tvProteinPercent);
        tvCarbsPercent = view.findViewById(R.id.tvCarbsPercent);
        tvFatPercent = view.findViewById(R.id.tvFatPercent);
        tvProteinValue = view.findViewById(R.id.tvProteinValue);
        tvCarbsValue = view.findViewById(R.id.tvCarbsValue);
        tvFatValue = view.findViewById(R.id.tvFatValue);

        updateDateDisplay();
    }

    private void setupDateNavigation() {
        btnPrevDay.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selectedDate);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            selectedDate = cal.getTimeInMillis();
            updateDateDisplay();
            loadData();
            loadChartData();
            updatePagerData();
        });

        btnNextDay.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selectedDate);
            cal.add(Calendar.DAY_OF_MONTH, 1);

            if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
                selectedDate = cal.getTimeInMillis();
                updateDateDisplay();
                loadData();
                loadChartData();
                updatePagerData();
            }
        });
    }

    private void setupCharts() {
        // Setup chart configurations
        ChartHelper.setupBarChart(chartBar, requireContext());
        ChartHelper.setupPieChart(chartPie, requireContext());
        ChartHelper.setupLineChart(chartLine, requireContext());

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
    }

    private void updateDateDisplay() {
        tvSelectedDate.setText(DateUtils.getDisplayDate(selectedDate));
    }

    private void setupViewPager() {
        pagerAdapter = new DiaryFragmentPagerAdapter(this, selectedDate);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Thực phẩm");
            } else {
                tab.setText("Bài tập");
            }
        }).attach();
    }

    private void updatePagerData() {
        pagerAdapter.updateDate(selectedDate);
    }

    private void loadData() {
        long startOfDay = DateUtils.getStartOfDay(selectedDate);
        long endOfDay = DateUtils.getEndOfDay(selectedDate);
        
        // Hiển thị calorie goal
        int calorieGoal = userPreferences.getDailyCalorieGoal();
        if (tvCalorieGoal != null) {
            tvCalorieGoal.setText(getString(R.string.goal_format, calorieGoal));
        }

        LiveData<Float> consumedLiveData = foodEntryRepository.getTotalCaloriesByDate(startOfDay, endOfDay);
        consumedLiveData.observe(getViewLifecycleOwner(), consumed -> {
            cachedConsumed = consumed != null ? consumed : 0f;
            tvTotalConsumed.setText(String.valueOf((int) cachedConsumed));
            updateNetCalories();
        });

        LiveData<Float> burnedLiveData = workoutEntryRepository.getTotalCaloriesBurnedByDate(startOfDay, endOfDay);
        burnedLiveData.observe(getViewLifecycleOwner(), burned -> {
            cachedBurned = burned != null ? burned : 0f;
            tvTotalBurned.setText(String.valueOf((int) cachedBurned));
            updateNetCalories();
        });

        // Load chart data
        loadChartData();
    }

    private void loadChartData() {
        long startOfDay = DateUtils.getStartOfDay(selectedDate);
        long endOfDay = DateUtils.getEndOfDay(selectedDate);

        // BarChart - Meal type comparison
        foodEntryRepository.getCaloriesByMealType(startOfDay, endOfDay)
            .observe(getViewLifecycleOwner(), mealData -> {
                ChartHelper.updateBarChartData(chartBar, mealData, requireContext());
            });

        // PieChart - Macro distribution
        foodEntryRepository.getMacroSummary(startOfDay, endOfDay)
            .observe(getViewLifecycleOwner(), macroData -> {
                ChartHelper.updatePieChartData(chartPie, macroData, requireContext());
                // Update macro progress bars
                updateMacroProgress(macroData);
            });

        // LineChart - Hourly trend
        foodEntryRepository.getHourlyCaloriesSummary(startOfDay, endOfDay)
            .observe(getViewLifecycleOwner(), hourlyData -> {
                ChartHelper.updateHourlyLineChartData(chartLine, hourlyData, requireContext());
            });
    }

    private void updateNetCalories() {
        int netCalories = (int) (cachedConsumed - cachedBurned);
        tvNetCalories.setText(String.valueOf(netCalories));
        
        // Cập nhật remaining và progress
        int calorieGoal = userPreferences.getDailyCalorieGoal();
        int remaining = calorieGoal - netCalories;
        
        if (tvCaloriesRemaining != null) {
            tvCaloriesRemaining.setText(String.valueOf(remaining));
        }
        
        // Tính progress percentage
        int progress = calorieGoal > 0 ? (int) ((netCalories / (float) calorieGoal) * 100) : 0;
        
        // Cập nhật phần trăm tiến độ (giống HomeFragment)
        updateProgressText(progress);
        
        // Cập nhật OverflowProgressBar (hỗ trợ overflow > 100%)
        if (progressCalories != null) {
            progressCalories.setProgress(Math.max(progress, 0));
        }
    }

    /**
     * Cập nhật text hiển thị phần trăm tiến độ và đổi màu khi vượt mục tiêu
     * @param progress Phần trăm tiến độ (0-200+)
     */
    private void updateProgressText(int progress) {
        if (tvProgressPercent == null) return;
        
        // Hiển thị text: cap tại 200%+
        if (progress > 200) {
            tvProgressPercent.setText("200%+");
        } else {
            tvProgressPercent.setText(progress + "%");
        }
        
        // Đổi màu text khi vượt mục tiêu (>100%)
        int textColor = progress > 100 
            ? ContextCompat.getColor(requireContext(), R.color.error)
            : ContextCompat.getColor(requireContext(), R.color.primary);
        tvProgressPercent.setTextColor(textColor);
    }

    /**
     * Tính macro goals từ calorie goal
     * Default ratio: 25% protein, 50% carbs, 25% fat
     */
    private int[] calculateMacroGoals() {
        int calorieGoal = userPreferences.getDailyCalorieGoal();
        
        // Protein: 25% of calories, 4 cal/g
        int proteinGoal = (int) ((calorieGoal * 0.25) / 4);
        
        // Carbs: 50% of calories, 4 cal/g
        int carbsGoal = (int) ((calorieGoal * 0.50) / 4);
        
        // Fat: 25% of calories, 9 cal/g
        int fatGoal = (int) ((calorieGoal * 0.25) / 9);
        
        return new int[] { proteinGoal, carbsGoal, fatGoal };
    }

    /**
     * Cập nhật macro progress bars
     */
    private void updateMacroProgress(MacroSum macroData) {
        int[] goals = calculateMacroGoals();
        int proteinGoal = goals[0];
        int carbsGoal = goals[1];
        int fatGoal = goals[2];
        
        float protein = macroData != null ? macroData.getProtein() : 0;
        float carbs = macroData != null ? macroData.getCarbs() : 0;
        float fat = macroData != null ? macroData.getFat() : 0;
        
        // Protein progress
        int proteinPercent = proteinGoal > 0 ? (int) ((protein / proteinGoal) * 100) : 0;
        if (tvProteinPercent != null) {
            tvProteinPercent.setText(Math.min(proteinPercent, 999) + "%");
        }
        if (progressProtein != null) {
            progressProtein.setProgress(Math.max(proteinPercent, 0));
        }
        if (tvProteinValue != null) {
            tvProteinValue.setText(String.format("%dg / %dg", (int) protein, proteinGoal));
        }
        
        // Carbs progress
        int carbsPercent = carbsGoal > 0 ? (int) ((carbs / carbsGoal) * 100) : 0;
        if (tvCarbsPercent != null) {
            tvCarbsPercent.setText(Math.min(carbsPercent, 999) + "%");
        }
        if (progressCarbs != null) {
            progressCarbs.setProgress(Math.max(carbsPercent, 0));
        }
        if (tvCarbsValue != null) {
            tvCarbsValue.setText(String.format("%dg / %dg", (int) carbs, carbsGoal));
        }
        
        // Fat progress
        int fatPercent = fatGoal > 0 ? (int) ((fat / fatGoal) * 100) : 0;
        if (tvFatPercent != null) {
            tvFatPercent.setText(Math.min(fatPercent, 999) + "%");
        }
        if (progressFat != null) {
            progressFat.setProgress(Math.max(fatPercent, 0));
        }
        if (tvFatValue != null) {
            tvFatValue.setText(String.format("%dg / %dg", (int) fat, fatGoal));
        }
    }

    public long getSelectedDate() {
        return selectedDate;
    }
}
