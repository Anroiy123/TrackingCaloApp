package com.example.trackingcaloapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.example.trackingcaloapp.utils.CalorieCalculator;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Fragment hiển thị trang chủ - Dashboard calo
 */
public class HomeFragment extends Fragment {

    private TextView tvDate;
    private TextView tvNetCalories;
    private TextView tvCaloriesConsumed;
    private TextView tvCaloriesBurned;
    private TextView tvCaloriesRemaining;
    private TextView tvGoalInfo;
    private ProgressBar progressCalories;

    // Macro nutrients views
    private TextView tvProteinProgress;
    private TextView tvCarbsProgress;
    private TextView tvFatProgress;
    private ProgressBar progressProtein;
    private ProgressBar progressCarbs;
    private ProgressBar progressFat;

    private RecyclerView rvRecentActivities;
    private TextView tvNoActivities;
    private MaterialButton btnAddFood;
    private MaterialButton btnAddWorkout;

    private UserPreferences userPreferences;
    private FoodEntryRepository foodEntryRepository;
    private WorkoutEntryRepository workoutEntryRepository;
    private RecentActivityAdapter recentActivityAdapter;

    private List<FoodEntry> cachedFoodEntries;
    private List<WorkoutEntry> cachedWorkoutEntries;

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
        loadTodayData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTodayData();
    }

    private void initViews(View view) {
        tvDate = view.findViewById(R.id.tvDate);
        tvNetCalories = view.findViewById(R.id.tvNetCalories);
        tvCaloriesConsumed = view.findViewById(R.id.tvCaloriesConsumed);
        tvCaloriesBurned = view.findViewById(R.id.tvCaloriesBurned);
        tvCaloriesRemaining = view.findViewById(R.id.tvCaloriesRemaining);
        tvGoalInfo = view.findViewById(R.id.tvGoalInfo);
        progressCalories = view.findViewById(R.id.progressCalories);

        // Macro nutrients
        tvProteinProgress = view.findViewById(R.id.tvProteinProgress);
        tvCarbsProgress = view.findViewById(R.id.tvCarbsProgress);
        tvFatProgress = view.findViewById(R.id.tvFatProgress);
        progressProtein = view.findViewById(R.id.progressProtein);
        progressCarbs = view.findViewById(R.id.progressCarbs);
        progressFat = view.findViewById(R.id.progressFat);

        rvRecentActivities = view.findViewById(R.id.rvRecentActivities);
        tvNoActivities = view.findViewById(R.id.tvNoActivities);
        btnAddFood = view.findViewById(R.id.btnAddFood);
        btnAddWorkout = view.findViewById(R.id.btnAddWorkout);

        // Setup RecyclerView
        recentActivityAdapter = new RecentActivityAdapter();
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentActivities.setAdapter(recentActivityAdapter);

        // Set today's date
        tvDate.setText(DateUtils.getDisplayDate(System.currentTimeMillis()));
    }

    private void setupButtons() {
        btnAddFood.setOnClickListener(v -> {
            // Navigate to Add Food tab
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(MainViewPagerAdapter.TAB_ADD);
            }
        });

        btnAddWorkout.setOnClickListener(v -> {
            // Show Add Workout dialog
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showAddWorkoutDialog();
            }
        });
    }

    private void loadTodayData() {
        long startOfDay = DateUtils.getStartOfDay(System.currentTimeMillis());
        long endOfDay = DateUtils.getEndOfDay(System.currentTimeMillis());
        int calorieGoal = userPreferences.getDailyCalorieGoal();

        // Calculate macro goals (30/40/30 ratio)
        float[] macroGoals = CalorieCalculator.calculateMacroGoals(calorieGoal);
        float proteinGoal = macroGoals[0];
        float carbsGoal = macroGoals[1];
        float fatGoal = macroGoals[2];

        // Update goal info
        tvGoalInfo.setText(getString(R.string.goal_format, calorieGoal));

        // Observe calories consumed
        LiveData<Float> consumedLiveData = foodEntryRepository.getTotalCaloriesByDate(startOfDay, endOfDay);
        consumedLiveData.observe(getViewLifecycleOwner(), consumed -> {
            float caloriesConsumed = consumed != null ? consumed : 0f;
            tvCaloriesConsumed.setText(String.valueOf((int) caloriesConsumed));
            updateNetCalories(calorieGoal);
        });

        // Observe calories burned
        LiveData<Float> burnedLiveData = workoutEntryRepository.getTotalCaloriesBurnedByDate(startOfDay, endOfDay);
        burnedLiveData.observe(getViewLifecycleOwner(), burned -> {
            float caloriesBurned = burned != null ? burned : 0f;
            tvCaloriesBurned.setText(String.valueOf((int) caloriesBurned));
            updateNetCalories(calorieGoal);
        });

        // Observe macro nutrients
        LiveData<Float> proteinLiveData = foodEntryRepository.getTotalProteinByDate(startOfDay, endOfDay);
        proteinLiveData.observe(getViewLifecycleOwner(), protein -> {
            float proteinTotal = protein != null ? protein : 0f;
            updateMacroProgress(tvProteinProgress, progressProtein, proteinTotal, proteinGoal);
        });

        LiveData<Float> carbsLiveData = foodEntryRepository.getTotalCarbsByDate(startOfDay, endOfDay);
        carbsLiveData.observe(getViewLifecycleOwner(), carbs -> {
            float carbsTotal = carbs != null ? carbs : 0f;
            updateMacroProgress(tvCarbsProgress, progressCarbs, carbsTotal, carbsGoal);
        });

        LiveData<Float> fatLiveData = foodEntryRepository.getTotalFatByDate(startOfDay, endOfDay);
        fatLiveData.observe(getViewLifecycleOwner(), fat -> {
            float fatTotal = fat != null ? fat : 0f;
            updateMacroProgress(tvFatProgress, progressFat, fatTotal, fatGoal);
        });

        // Observe food entries for recent activities
        LiveData<List<FoodEntry>> foodEntriesLiveData = foodEntryRepository.getEntriesByDate(startOfDay, endOfDay);
        foodEntriesLiveData.observe(getViewLifecycleOwner(), foodEntries -> {
            updateRecentActivities(foodEntries, null);
        });

        // Observe workout entries for recent activities
        LiveData<List<WorkoutEntry>> workoutEntriesLiveData = workoutEntryRepository.getEntriesByDate(startOfDay, endOfDay);
        workoutEntriesLiveData.observe(getViewLifecycleOwner(), workoutEntries -> {
            updateRecentActivities(null, workoutEntries);
        });
    }

    private void updateMacroProgress(TextView tvProgress, ProgressBar progressBar, float current, float goal) {
        tvProgress.setText(String.format("%.0fg / %.0fg", current, goal));
        int progress = goal > 0 ? (int) ((current / goal) * 100) : 0;
        progress = Math.min(100, progress);
        progressBar.setProgress(progress);
    }

    private void updateNetCalories(int calorieGoal) {
        String consumedText = tvCaloriesConsumed.getText().toString();
        String burnedText = tvCaloriesBurned.getText().toString();

        int consumed = consumedText.isEmpty() ? 0 : Integer.parseInt(consumedText);
        int burned = burnedText.isEmpty() ? 0 : Integer.parseInt(burnedText);

        int netCalories = consumed - burned;

        // Công thức mới: Remaining = (Goal + Burned) - Consumed
        int remaining = (int) CalorieCalculator.calculateRemainingCalories(calorieGoal, consumed, burned);

        tvNetCalories.setText(String.valueOf(netCalories));

        // Hiển thị remaining
        tvCaloriesRemaining.setText(String.valueOf(remaining));

        // Update progress bar
        int totalAllowance = calorieGoal + burned;
        int progress = totalAllowance > 0 ? (consumed * 100) / totalAllowance : 0;
        progress = Math.max(0, Math.min(100, progress));
        progressCalories.setProgress(progress);
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

