package com.example.trackingcaloapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.trackingcaloapp.ui.diary.DiaryActivity;
import com.example.trackingcaloapp.ui.onboarding.OnboardingActivity;
import com.example.trackingcaloapp.ui.profile.ProfileActivity;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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
    private BottomNavigationView bottomNavigation;

    private UserPreferences userPreferences;
    private FoodEntryRepository foodEntryRepository;
    private WorkoutEntryRepository workoutEntryRepository;

    private RecentActivityAdapter recentActivityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize preferences
        userPreferences = new UserPreferences(this);

        // Check if onboarding is complete
        if (!userPreferences.isOnboardingComplete()) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }

        // Initialize repositories
        AppDatabase db = AppDatabase.getInstance(this);
        foodEntryRepository = new FoodEntryRepository(db.foodEntryDao());
        workoutEntryRepository = new WorkoutEntryRepository(db.workoutEntryDao());

        // Initialize views
        initViews();
        setupBottomNavigation();
        setupButtons();
        
        // Load data
        loadTodayData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this activity
        if (userPreferences.isOnboardingComplete()) {
            loadTodayData();
        }
    }

    private void initViews() {
        tvDate = findViewById(R.id.tvDate);
        tvNetCalories = findViewById(R.id.tvNetCalories);
        tvCaloriesConsumed = findViewById(R.id.tvCaloriesConsumed);
        tvCaloriesBurned = findViewById(R.id.tvCaloriesBurned);
        tvCaloriesRemaining = findViewById(R.id.tvCaloriesRemaining);
        tvGoalInfo = findViewById(R.id.tvGoalInfo);
        progressCalories = findViewById(R.id.progressCalories);
        rvRecentActivities = findViewById(R.id.rvRecentActivities);
        tvNoActivities = findViewById(R.id.tvNoActivities);
        btnAddFood = findViewById(R.id.btnAddFood);
        btnAddWorkout = findViewById(R.id.btnAddWorkout);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Setup RecyclerView
        recentActivityAdapter = new RecentActivityAdapter();
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(this));
        rvRecentActivities.setAdapter(recentActivityAdapter);

        // Set today's date
        tvDate.setText(DateUtils.getDisplayDate(System.currentTimeMillis()));
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_diary) {
                    startActivity(new Intent(MainActivity.this, DiaryActivity.class));
                    return true;
                } else if (itemId == R.id.nav_add) {
                    startActivity(new Intent(MainActivity.this, AddFoodActivity.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void setupButtons() {
        btnAddFood.setOnClickListener(v -> {
            startActivity(new Intent(this, AddFoodActivity.class));
        });

        btnAddWorkout.setOnClickListener(v -> {
            startActivity(new Intent(this, AddWorkoutActivity.class));
        });
    }

    private void loadTodayData() {
        long startOfDay = DateUtils.getStartOfDay(System.currentTimeMillis());
        long endOfDay = DateUtils.getEndOfDay(System.currentTimeMillis());
        int calorieGoal = userPreferences.getDailyCalorieGoal();

        // Update goal info
        tvGoalInfo.setText(getString(R.string.goal_format, calorieGoal));

        // Observe calories consumed
        LiveData<Float> consumedLiveData = foodEntryRepository.getTotalCaloriesByDate(startOfDay, endOfDay);
        consumedLiveData.observe(this, consumed -> {
            float caloriesConsumed = consumed != null ? consumed : 0f;
            tvCaloriesConsumed.setText(String.valueOf((int) caloriesConsumed));
            updateNetCalories(calorieGoal);
        });

        // Observe calories burned
        LiveData<Float> burnedLiveData = workoutEntryRepository.getTotalCaloriesBurnedByDate(startOfDay, endOfDay);
        burnedLiveData.observe(this, burned -> {
            float caloriesBurned = burned != null ? burned : 0f;
            tvCaloriesBurned.setText(String.valueOf((int) caloriesBurned));
            updateNetCalories(calorieGoal);
        });

        // Observe food entries for recent activities
        LiveData<List<FoodEntry>> foodEntriesLiveData = foodEntryRepository.getEntriesByDate(startOfDay, endOfDay);
        foodEntriesLiveData.observe(this, foodEntries -> {
            updateRecentActivities(foodEntries, null);
        });

        // Observe workout entries for recent activities
        LiveData<List<WorkoutEntry>> workoutEntriesLiveData = workoutEntryRepository.getEntriesByDate(startOfDay, endOfDay);
        workoutEntriesLiveData.observe(this, workoutEntries -> {
            updateRecentActivities(null, workoutEntries);
        });
    }

    private void updateNetCalories(int calorieGoal) {
        String consumedText = tvCaloriesConsumed.getText().toString();
        String burnedText = tvCaloriesBurned.getText().toString();
        
        int consumed = consumedText.isEmpty() ? 0 : Integer.parseInt(consumedText);
        int burned = burnedText.isEmpty() ? 0 : Integer.parseInt(burnedText);
        
        int netCalories = consumed - burned;
        int remaining = calorieGoal - netCalories;
        
        tvNetCalories.setText(String.valueOf(netCalories));
        tvCaloriesRemaining.setText(String.valueOf(remaining));
        
        // Update progress bar
        int progress = calorieGoal > 0 ? (netCalories * 100) / calorieGoal : 0;
        progress = Math.max(0, Math.min(100, progress));
        progressCalories.setProgress(progress);
    }

    private List<FoodEntry> cachedFoodEntries;
    private List<WorkoutEntry> cachedWorkoutEntries;

    private void updateRecentActivities(List<FoodEntry> foodEntries, List<WorkoutEntry> workoutEntries) {
        if (foodEntries != null) {
            cachedFoodEntries = foodEntries;
        }
        if (workoutEntries != null) {
            cachedWorkoutEntries = workoutEntries;
        }

        // Check if we have any activities
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

