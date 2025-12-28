package com.example.trackingcaloapp.ui.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
    private TextView tvCalorieGoal, tvCaloriesRemaining;
    private ProgressBar progressCalories;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

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
        progressCalories = view.findViewById(R.id.progressCalories);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Chart views
        tabChartType = view.findViewById(R.id.tabChartType);
        chartFlipper = view.findViewById(R.id.chartFlipper);
        chartBar = view.findViewById(R.id.chartBar);
        chartPie = view.findViewById(R.id.chartPie);
        chartLine = view.findViewById(R.id.chartLine);

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
            tvCalorieGoal.setText(String.valueOf(calorieGoal));
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
        
        if (progressCalories != null) {
            int progress = calorieGoal > 0 ? (int) ((netCalories / (float) calorieGoal) * 100) : 0;
            progressCalories.setProgress(Math.min(Math.max(progress, 0), 100));
        }
    }

    public long getSelectedDate() {
        return selectedDate;
    }
}
