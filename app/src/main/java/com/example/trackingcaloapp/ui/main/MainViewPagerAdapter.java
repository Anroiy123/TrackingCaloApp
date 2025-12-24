package com.example.trackingcaloapp.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.trackingcaloapp.ui.addfood.AddFoodFragment;
import com.example.trackingcaloapp.ui.diary.DiaryFragment;
import com.example.trackingcaloapp.ui.profile.ProfileFragment;

/**
 * Adapter quản lý các Fragment chính của app.
 * Sử dụng ViewPager2 để chuyển đổi mượt mà giữa các tab.
 */
public class MainViewPagerAdapter extends FragmentStateAdapter {

    public static final int TAB_HOME = 0;
    public static final int TAB_DIARY = 1;
    public static final int TAB_ADD = 2;
    public static final int TAB_PROFILE = 3;

    private static final int NUM_PAGES = 4;

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case TAB_HOME:
                return new HomeFragment();
            case TAB_DIARY:
                return new DiaryFragment();
            case TAB_ADD:
                return new AddFoodFragment();
            case TAB_PROFILE:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}

