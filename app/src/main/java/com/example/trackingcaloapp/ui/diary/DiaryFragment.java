package com.example.trackingcaloapp.ui.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.repository.FoodEntryRepository;
import com.example.trackingcaloapp.data.repository.WorkoutEntryRepository;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;

public class DiaryFragment extends Fragment {

    private MaterialButton btnPrevDay, btnNextDay;
    private TextView tvSelectedDate;
    private TextView tvTotalConsumed, tvTotalBurned, tvNetCalories;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private FoodEntryRepository foodEntryRepository;
    private WorkoutEntryRepository workoutEntryRepository;

    private long selectedDate = System.currentTimeMillis();
    private DiaryFragmentPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        foodEntryRepository = new FoodEntryRepository(db.foodEntryDao());
        workoutEntryRepository = new WorkoutEntryRepository(db.workoutEntryDao());

        initViews(view);
        setupDateNavigation();
        setupViewPager();
        loadData();
    }


    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews(View view) {
        btnPrevDay = view.findViewById(R.id.btnPrevDay);
        btnNextDay = view.findViewById(R.id.btnNextDay);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvTotalConsumed = view.findViewById(R.id.tvTotalConsumed);
        tvTotalBurned = view.findViewById(R.id.tvTotalBurned);
        tvNetCalories = view.findViewById(R.id.tvNetCalories);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

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
        pagerAdapter = new DiaryFragmentPagerAdapter(this, selectedDate);
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

    private void loadData() {
        long startOfDay = DateUtils.getStartOfDay(selectedDate);
        long endOfDay = DateUtils.getEndOfDay(selectedDate);

        LiveData<Float> consumedLiveData = foodEntryRepository.getTotalCaloriesByDate(startOfDay, endOfDay);
        consumedLiveData.observe(getViewLifecycleOwner(), consumed -> {
            float caloriesConsumed = consumed != null ? consumed : 0f;
            tvTotalConsumed.setText(String.valueOf((int) caloriesConsumed));
            updateNetCalories();
        });

        LiveData<Float> burnedLiveData = workoutEntryRepository.getTotalCaloriesBurnedByDate(startOfDay, endOfDay);
        burnedLiveData.observe(getViewLifecycleOwner(), burned -> {
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
