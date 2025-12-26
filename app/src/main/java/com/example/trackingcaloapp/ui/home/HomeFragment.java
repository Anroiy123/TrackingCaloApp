package com.example.trackingcaloapp.ui.home;

import android.content.Intent;
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
import com.example.trackingcaloapp.ui.addfood.AddFoodActivity;
import com.example.trackingcaloapp.ui.addworkout.AddWorkoutActivity;
import com.example.trackingcaloapp.ui.main.RecentActivityAdapter;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HomeFragment extends Fragment {

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

    private UserPreferences userPreferences;
    private FoodEntryRepository foodEntryRepository;
    private WorkoutEntryRepository workoutEntryRepository;
    private RecentActivityAdapter recentActivityAdapter;

    // Cached values for calculation
    private float cachedConsumed = 0f;
    private float cachedBurned = 0f;
    private List<FoodEntry> cachedFoodEntries;
    private List<WorkoutEntry> cachedWorkoutEntries;

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
        rvRecentActivities = view.findViewById(R.id.rvRecentActivities);
        tvNoActivities = view.findViewById(R.id.tvNoActivities);
        btnAddFood = view.findViewById(R.id.btnAddFood);
        btnAddWorkout = view.findViewById(R.id.btnAddWorkout);

        tvDate.setText(DateUtils.getDisplayDate(System.currentTimeMillis()));

        recentActivityAdapter = new RecentActivityAdapter();
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentActivities.setAdapter(recentActivityAdapter);
    }

    private void setupButtons() {
        btnAddFood.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddFoodActivity.class));
        });

        btnAddWorkout.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddWorkoutActivity.class));
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
