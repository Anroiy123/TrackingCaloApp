package com.example.trackingcaloapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.data.repository.FoodEntryRepository;
import com.example.trackingcaloapp.data.repository.WorkoutEntryRepository;
import com.example.trackingcaloapp.model.DailyCalorieSum;
import com.example.trackingcaloapp.model.MacroSum;
import com.example.trackingcaloapp.model.MealTypeCalories;
import com.example.trackingcaloapp.ui.main.RecentActivityAdapter;
import com.example.trackingcaloapp.utils.ChartHelper;
import com.example.trackingcaloapp.utils.DateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class HomeFragment extends Fragment {

    // Interface để communicate với MainActivity
    public interface OnNavigationListener {
        void navigateToAddFood();
        void navigateToAddWorkout();
    }

    private OnNavigationListener navigationListener;

    private TextView tvDate;
    private TextView tvNetCalories;
    private TextView tvCaloriesConsumed;
    private TextView tvCaloriesBurned;
    private TextView tvCaloriesRemaining;
    private TextView tvGoalInfo;
    private ProgressBar progressCalories;
    private RecyclerView rvRecentActivities;
    private TextView tvNoActivities;
    private MaterialButton btnAddFood;
    private MaterialButton btnAddWorkout;

    // Chart views
    private TabLayout tabChartType;
    private ViewFlipper chartFlipper;
    private LineChart chartLine;
    private BarChart chartBar;
    private PieChart chartPie;

    private UserPreferences userPreferences;
    private FoodEntryRepository foodEntryRepository;
    private WorkoutEntryRepository workoutEntryRepository;
    private RecentActivityAdapter recentActivityAdapter;

    // Cached values for calculation
    private float cachedConsumed = 0f;
    private float cachedBurned = 0f;
    private List<FoodEntry> cachedFoodEntries;
    private List<WorkoutEntry> cachedWorkoutEntries;

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
        setupButtons();
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
        tvDate = view.findViewById(R.id.tvDate);
        tvNetCalories = view.findViewById(R.id.tvNetCalories);
        tvCaloriesConsumed = view.findViewById(R.id.tvCaloriesConsumed);
        tvCaloriesBurned = view.findViewById(R.id.tvCaloriesBurned);
        tvCaloriesRemaining = view.findViewById(R.id.tvCaloriesRemaining);
        tvGoalInfo = view.findViewById(R.id.tvGoalInfo);
        progressCalories = view.findViewById(R.id.progressCalories);
        rvRecentActivities = view.findViewById(R.id.rvRecentActivities);
        tvNoActivities = view.findViewById(R.id.tvNoActivities);
        btnAddFood = view.findViewById(R.id.btnAddFood);
        btnAddWorkout = view.findViewById(R.id.btnAddWorkout);

        tvDate.setText(DateUtils.getDisplayDate(System.currentTimeMillis()));

        recentActivityAdapter = new RecentActivityAdapter();
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentActivities.setAdapter(recentActivityAdapter);

        // Chart views
        tabChartType = view.findViewById(R.id.tabChartType);
        chartFlipper = view.findViewById(R.id.chartFlipper);
        chartLine = view.findViewById(R.id.chartLine);
        chartBar = view.findViewById(R.id.chartBar);
        chartPie = view.findViewById(R.id.chartPie);
    }

    private void setupButtons() {
        btnAddFood.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.navigateToAddFood();
            }
        });

        btnAddWorkout.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.navigateToAddWorkout();
            }
        });
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
    }

    private void loadChartData() {
        // Get 7 days range
        long startDate = ChartHelper.getWeekStartTimestamp();
        long endDate = ChartHelper.getWeekEndTimestamp();

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

        LiveData<List<FoodEntry>> foodEntriesLiveData = foodEntryRepository.getEntriesByDate(startOfDay, endOfDay);
        foodEntriesLiveData.observe(getViewLifecycleOwner(), foodEntries -> {
            updateRecentActivities(foodEntries, null);
        });

        LiveData<List<WorkoutEntry>> workoutEntriesLiveData = workoutEntryRepository.getEntriesByDate(startOfDay, endOfDay);
        workoutEntriesLiveData.observe(getViewLifecycleOwner(), workoutEntries -> {
            updateRecentActivities(null, workoutEntries);
        });
    }

    private void updateNetCalories(int calorieGoal) {
        float netCalories = cachedConsumed - cachedBurned;
        float remaining = calorieGoal - netCalories;

        tvNetCalories.setText(String.valueOf((int) netCalories));
        tvCaloriesRemaining.setText(String.valueOf((int) remaining));

        int progress = calorieGoal > 0 ? (int) ((netCalories / calorieGoal) * 100) : 0;
        progressCalories.setProgress(Math.min(progress, 100));
    }

    private void updateRecentActivities(List<FoodEntry> foodEntries, List<WorkoutEntry> workoutEntries) {
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
