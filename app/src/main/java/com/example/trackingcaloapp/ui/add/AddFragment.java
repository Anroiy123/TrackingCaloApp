package com.example.trackingcaloapp.ui.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trackingcaloapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AddFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AddPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        setupViewPager();
    }

    private void setupViewPager() {
        pagerAdapter = new AddPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Thực phẩm");
                tab.setIcon(android.R.drawable.ic_menu_add);
            } else {
                tab.setText("Bài tập");
                tab.setIcon(android.R.drawable.ic_menu_compass);
            }
        }).attach();
    }

    /**
     * Set tab hiện tại (0 = Food, 1 = Workout)
     * Được gọi từ HomeFragment khi user click nút Add Food/Workout
     */
    public void setCurrentTab(int position) {
        if (viewPager != null && position >= 0 && position < 2) {
            viewPager.setCurrentItem(position, false);
        }
    }
}
