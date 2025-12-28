package com.example.trackingcaloapp.ui.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trackingcaloapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AddFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AddPagerAdapter pagerAdapter;

    // Custom tab views
    private MaterialCardView cardFoodTab;
    private MaterialCardView cardWorkoutTab;
    private ImageView ivFoodIcon;
    private ImageView ivWorkoutIcon;
    private TextView tvFoodLabel;
    private TextView tvWorkoutLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewPager();
        setupCustomTabs();
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Custom tab views
        cardFoodTab = view.findViewById(R.id.cardFoodTab);
        cardWorkoutTab = view.findViewById(R.id.cardWorkoutTab);
        ivFoodIcon = view.findViewById(R.id.ivFoodIcon);
        ivWorkoutIcon = view.findViewById(R.id.ivWorkoutIcon);
        tvFoodLabel = view.findViewById(R.id.tvFoodLabel);
        tvWorkoutLabel = view.findViewById(R.id.tvWorkoutLabel);
    }

    private void setupViewPager() {
        pagerAdapter = new AddPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Hidden TabLayout for ViewPager sync
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Empty - tabs are hidden
        }).attach();

        // Listen to page changes to update custom tabs
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTabStyles(position);
            }
        });
    }

    private void setupCustomTabs() {
        cardFoodTab.setOnClickListener(v -> {
            viewPager.setCurrentItem(0, true);
        });

        cardWorkoutTab.setOnClickListener(v -> {
            viewPager.setCurrentItem(1, true);
        });

        // Initial state
        updateTabStyles(0);
    }

    private void updateTabStyles(int selectedPosition) {
        if (selectedPosition == 0) {
            // Food selected
            cardFoodTab.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary));
            cardFoodTab.setCardElevation(getResources().getDimension(R.dimen.elevation_sm));
            ivFoodIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
            tvFoodLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

            // Workout unselected
            cardWorkoutTab.setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
            cardWorkoutTab.setCardElevation(0);
            ivWorkoutIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            tvWorkoutLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        } else {
            // Workout selected
            cardWorkoutTab.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary));
            cardWorkoutTab.setCardElevation(getResources().getDimension(R.dimen.elevation_sm));
            ivWorkoutIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
            tvWorkoutLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

            // Food unselected
            cardFoodTab.setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
            cardFoodTab.setCardElevation(0);
            ivFoodIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            tvFoodLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        }
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
