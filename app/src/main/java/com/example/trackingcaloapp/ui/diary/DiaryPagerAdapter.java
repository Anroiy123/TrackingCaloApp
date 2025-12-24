package com.example.trackingcaloapp.ui.diary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DiaryPagerAdapter extends FragmentStateAdapter {

    private long selectedDate;
    private FoodEntriesFragment foodFragment;
    private WorkoutEntriesFragment workoutFragment;

    // Constructor cho FragmentActivity (deprecated, keep for backward compatibility)
    public DiaryPagerAdapter(@NonNull FragmentActivity fragmentActivity, long selectedDate) {
        super(fragmentActivity);
        this.selectedDate = selectedDate;
    }

    // Constructor cho Fragment parent (nested fragments trong DiaryFragment)
    public DiaryPagerAdapter(@NonNull Fragment fragment, long selectedDate) {
        super(fragment);
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        args.putLong("selectedDate", selectedDate);

        if (position == 0) {
            foodFragment = new FoodEntriesFragment();
            foodFragment.setArguments(args);
            return foodFragment;
        } else {
            workoutFragment = new WorkoutEntriesFragment();
            workoutFragment.setArguments(args);
            return workoutFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void updateDate(long newDate) {
        this.selectedDate = newDate;
        if (foodFragment != null) {
            foodFragment.updateDate(newDate);
        }
        if (workoutFragment != null) {
            workoutFragment.updateDate(newDate);
        }
    }
}

