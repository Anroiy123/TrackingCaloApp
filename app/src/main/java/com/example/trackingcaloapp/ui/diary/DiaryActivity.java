package com.example.trackingcaloapp.ui.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.repository.FoodEntryRepository;
import com.example.trackingcaloapp.data.repository.WorkoutEntryRepository;
import com.example.trackingcaloapp.ui.addfood.AddFoodActivity;
import com.example.trackingcaloapp.ui.main.MainActivity;
import com.example.trackingcaloapp.ui.profile.ProfileActivity;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;

public class DiaryActivity extends AppCompatActivity {

    private ImageButton btnPrevDay, btnNextDay;
    private TextView tvSelectedDate;
    private TextView tvTotalConsumed, tvTotalBurned, tvNetCalories;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    private FoodEntryRepository foodEntryRepository;
    private WorkoutEntryRepository workoutEntryRepository;

    private long selectedDate = System.currentTimeMillis();
    private DiaryPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // Initialize repositories
        AppDatabase db = AppDatabase.getInstance(this);
        foodEntryRepository = new FoodEntryRepository(db.foodEntryDao());
        workoutEntryRepository = new WorkoutEntryRepository(db.workoutEntryDao());

        initViews();
        setupDateNavigation();
        setupViewPager();
        setupBottomNavigation();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        btnPrevDay = findViewById(R.id.btnPrevDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvTotalConsumed = findViewById(R.id.tvTotalConsumed);
        tvTotalBurned = findViewById(R.id.tvTotalBurned);
        tvNetCalories = findViewById(R.id.tvNetCalories);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);

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
            updatePagerData();
        });

        btnNextDay.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selectedDate);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            
            // Don't allow future dates
            if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
                selectedDate = cal.getTimeInMillis();
                updateDateDisplay();
                loadData();
                updatePagerData();
            }
        });
    }

    private void updateDateDisplay() {
        tvSelectedDate.setText(DateUtils.getDisplayDate(selectedDate));
    }

    private void setupViewPager() {
        pagerAdapter = new DiaryPagerAdapter(this, selectedDate);
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

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_diary);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(DiaryActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_diary) {
                    return true;
                } else if (itemId == R.id.nav_add) {
                    startActivity(new Intent(DiaryActivity.this, AddFoodActivity.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(DiaryActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void loadData() {
        long startOfDay = DateUtils.getStartOfDay(selectedDate);
        long endOfDay = DateUtils.getEndOfDay(selectedDate);

        // Observe calories consumed
        LiveData<Float> consumedLiveData = foodEntryRepository.getTotalCaloriesByDate(startOfDay, endOfDay);
        consumedLiveData.observe(this, consumed -> {
            float caloriesConsumed = consumed != null ? consumed : 0f;
            tvTotalConsumed.setText(String.valueOf((int) caloriesConsumed));
            updateNetCalories();
        });

        // Observe calories burned
        LiveData<Float> burnedLiveData = workoutEntryRepository.getTotalCaloriesBurnedByDate(startOfDay, endOfDay);
        burnedLiveData.observe(this, burned -> {
            float caloriesBurned = burned != null ? burned : 0f;
            tvTotalBurned.setText(String.valueOf((int) caloriesBurned));
            updateNetCalories();
        });
    }

    private void updateNetCalories() {
        String consumedText = tvTotalConsumed.getText().toString();
        String burnedText = tvTotalBurned.getText().toString();
        
        int consumed = consumedText.isEmpty() ? 0 : Integer.parseInt(consumedText);
        int burned = burnedText.isEmpty() ? 0 : Integer.parseInt(burnedText);
        
        int netCalories = consumed - burned;
        tvNetCalories.setText(String.valueOf(netCalories));
    }

    public long getSelectedDate() {
        return selectedDate;
    }
}

