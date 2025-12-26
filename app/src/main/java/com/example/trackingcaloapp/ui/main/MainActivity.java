package com.example.trackingcaloapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.ui.add.AddFragment;
import com.example.trackingcaloapp.ui.diary.DiaryFragment;
import com.example.trackingcaloapp.ui.home.HomeFragment;
import com.example.trackingcaloapp.ui.onboarding.OnboardingActivity;
import com.example.trackingcaloapp.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private UserPreferences userPreferences;

    // Fragment instances để giữ state
    private HomeFragment homeFragment;
    private DiaryFragment diaryFragment;
    private AddFragment addFragment;
    private ProfileFragment profileFragment;
    private Fragment activeFragment;

    private static final String TAG_HOME = "home";
    private static final String TAG_DIARY = "diary";
    private static final String TAG_ADD = "add";
    private static final String TAG_PROFILE = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        userPreferences = new UserPreferences(this);

        // Check onboarding
        if (!userPreferences.isOnboardingComplete()) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }

        bottomNavigation = findViewById(R.id.bottomNavigation);

        if (savedInstanceState == null) {
            setupFragments();
        } else {
            restoreFragments();
        }

        setupBottomNavigation();
    }


    private void setupFragments() {
        FragmentManager fm = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        diaryFragment = new DiaryFragment();
        addFragment = new AddFragment();
        profileFragment = new ProfileFragment();

        // Add all fragments but hide all except home
        fm.beginTransaction()
                .add(R.id.fragmentContainer, profileFragment, TAG_PROFILE).hide(profileFragment)
                .add(R.id.fragmentContainer, addFragment, TAG_ADD).hide(addFragment)
                .add(R.id.fragmentContainer, diaryFragment, TAG_DIARY).hide(diaryFragment)
                .add(R.id.fragmentContainer, homeFragment, TAG_HOME)
                .commit();

        activeFragment = homeFragment;
    }

    private void restoreFragments() {
        FragmentManager fm = getSupportFragmentManager();

        homeFragment = (HomeFragment) fm.findFragmentByTag(TAG_HOME);
        diaryFragment = (DiaryFragment) fm.findFragmentByTag(TAG_DIARY);
        addFragment = (AddFragment) fm.findFragmentByTag(TAG_ADD);
        profileFragment = (ProfileFragment) fm.findFragmentByTag(TAG_PROFILE);

        // Find active fragment
        if (homeFragment != null && homeFragment.isVisible()) {
            activeFragment = homeFragment;
        } else if (diaryFragment != null && diaryFragment.isVisible()) {
            activeFragment = diaryFragment;
        } else if (addFragment != null && addFragment.isVisible()) {
            activeFragment = addFragment;
        } else if (profileFragment != null && profileFragment.isVisible()) {
            activeFragment = profileFragment;
        } else {
            activeFragment = homeFragment;
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = homeFragment;
                } else if (itemId == R.id.nav_diary) {
                    selectedFragment = diaryFragment;
                } else if (itemId == R.id.nav_add) {
                    selectedFragment = addFragment;
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = profileFragment;
                }

                if (selectedFragment != null && selectedFragment != activeFragment) {
                    switchFragment(selectedFragment);
                    return true;
                }

                return true;
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();

        activeFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        // If not on home, go to home first
        if (activeFragment != homeFragment) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
}
