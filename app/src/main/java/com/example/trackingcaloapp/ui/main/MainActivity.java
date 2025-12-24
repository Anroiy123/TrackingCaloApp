package com.example.trackingcaloapp.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.ui.addworkout.AddWorkoutDialogFragment;
import com.example.trackingcaloapp.ui.onboarding.OnboardingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity - Container duy nhất chứa ViewPager2 và BottomNavigationView.
 * Tất cả các màn hình khác đều là Fragment được quản lý bởi ViewPager2.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private MainViewPagerAdapter pagerAdapter;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPreferences = new UserPreferences(this);

        // Check if onboarding is complete
        if (!userPreferences.isOnboardingComplete()) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }

        initViews();
        setupViewPager();
        setupBottomNavigation();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupViewPager() {
        pagerAdapter = new MainViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Disable swipe để tránh conflict với các gesture khác trong Fragment
        viewPager.setUserInputEnabled(false);

        // Keep all fragments alive (tránh reload khi chuyển tab)
        viewPager.setOffscreenPageLimit(4);

        // Sync ViewPager với BottomNavigation
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case MainViewPagerAdapter.TAB_HOME:
                        bottomNavigation.setSelectedItemId(R.id.nav_home);
                        break;
                    case MainViewPagerAdapter.TAB_DIARY:
                        bottomNavigation.setSelectedItemId(R.id.nav_diary);
                        break;
                    case MainViewPagerAdapter.TAB_ADD:
                        bottomNavigation.setSelectedItemId(R.id.nav_add);
                        break;
                    case MainViewPagerAdapter.TAB_PROFILE:
                        bottomNavigation.setSelectedItemId(R.id.nav_profile);
                        break;
                }
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(MainViewPagerAdapter.TAB_HOME, false);
                return true;
            } else if (itemId == R.id.nav_diary) {
                viewPager.setCurrentItem(MainViewPagerAdapter.TAB_DIARY, false);
                return true;
            } else if (itemId == R.id.nav_add) {
                viewPager.setCurrentItem(MainViewPagerAdapter.TAB_ADD, false);
                return true;
            } else if (itemId == R.id.nav_profile) {
                viewPager.setCurrentItem(MainViewPagerAdapter.TAB_PROFILE, false);
                return true;
            }
            return false;
        });
    }

    /**
     * Navigate to a specific tab programmatically.
     * Được gọi từ các Fragment con.
     */
    public void navigateToTab(int tabIndex) {
        viewPager.setCurrentItem(tabIndex, false);
    }

    /**
     * Show Add Workout Bottom Sheet Dialog.
     * Được gọi từ HomeFragment khi user click nút "Thêm bài tập".
     */
    public void showAddWorkoutDialog() {
        AddWorkoutDialogFragment dialog = new AddWorkoutDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddWorkoutDialog");
    }
}

