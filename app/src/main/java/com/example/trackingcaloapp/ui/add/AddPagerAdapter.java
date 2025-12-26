package com.example.trackingcaloapp.ui.add;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AddPagerAdapter extends FragmentStateAdapter {

    public AddPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new AddFoodFragment();
        } else {
            return new AddWorkoutFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
