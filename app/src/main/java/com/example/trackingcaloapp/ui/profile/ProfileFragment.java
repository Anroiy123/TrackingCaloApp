package com.example.trackingcaloapp.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.entity.WeightLog;
import com.example.trackingcaloapp.model.UserInfo;
import com.example.trackingcaloapp.ui.login.LoginActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ProfileFragment với View Mode (read-only) và Edit Mode (long press).
 * Hiển thị thông tin cá nhân, mục tiêu, BMI và biểu đồ xu hướng cân nặng.
 */
public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;

    // Cards
    private MaterialCardView cardPersonalInfo, cardGoals, cardBMI, cardWeightTrend;

    // Personal Info Views
    private TextView tvName, tvAge, tvGender, tvHeight, tvWeight;

    // Goals Views
    private TextView tvActivityLevel, tvWeightGoal, tvCalorieGoal;

    // BMI Views
    private TextView tvBMI;
    private Chip chipBMICategory;

    // Weight Trend Views
    private LineChart chartWeightTrend;
    private TextView tvEmptyChart;

    // Actions
    private FloatingActionButton fabLogWeight;
    private MaterialButton btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        initViews(view);
        setupChart();
        setupLongPressListeners();
        setupClickListeners();
        observeData();
    }

    private void initViews(View view) {
        // Cards
        cardPersonalInfo = view.findViewById(R.id.cardPersonalInfo);
        cardGoals = view.findViewById(R.id.cardGoals);
        cardBMI = view.findViewById(R.id.cardBMI);
        cardWeightTrend = view.findViewById(R.id.cardWeightTrend);

        // Personal Info
        tvName = view.findViewById(R.id.tvName);
        tvAge = view.findViewById(R.id.tvAge);
        tvGender = view.findViewById(R.id.tvGender);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvWeight = view.findViewById(R.id.tvWeight);

        // Goals
        tvActivityLevel = view.findViewById(R.id.tvActivityLevel);
        tvWeightGoal = view.findViewById(R.id.tvWeightGoal);
        tvCalorieGoal = view.findViewById(R.id.tvCalorieGoal);

        // BMI
        tvBMI = view.findViewById(R.id.tvBMI);
        chipBMICategory = view.findViewById(R.id.chipBMICategory);

        // Weight Trend
        chartWeightTrend = view.findViewById(R.id.chartWeightTrend);
        tvEmptyChart = view.findViewById(R.id.tvEmptyChart);

        // Actions
        fabLogWeight = view.findViewById(R.id.fabLogWeight);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupChart() {
        chartWeightTrend.getDescription().setEnabled(false);
        chartWeightTrend.setTouchEnabled(true);
        chartWeightTrend.setDragEnabled(true);
        chartWeightTrend.setScaleEnabled(false);
        chartWeightTrend.setPinchZoom(false);
        chartWeightTrend.setDrawGridBackground(false);
        chartWeightTrend.getLegend().setEnabled(false);

        // X Axis
        XAxis xAxis = chartWeightTrend.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(requireContext().getColor(R.color.text_secondary));
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
            @Override
            public String getFormattedValue(float value) {
                return sdf.format(new Date((long) value));
            }
        });

        // Y Axis
        YAxis leftAxis = chartWeightTrend.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(requireContext().getColor(R.color.outline));
        leftAxis.setTextColor(requireContext().getColor(R.color.text_secondary));

        chartWeightTrend.getAxisRight().setEnabled(false);
    }

    private void setupLongPressListeners() {
        cardPersonalInfo.setOnLongClickListener(v -> {
            showEditPersonalInfoDialog();
            return true;
        });

        cardGoals.setOnLongClickListener(v -> {
            showEditGoalsDialog();
            return true;
        });
    }

    private void setupClickListeners() {
        fabLogWeight.setOnClickListener(v -> showQuickWeightLogDialog());

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void observeData() {
        viewModel.getUserInfo().observe(getViewLifecycleOwner(), this::updateUserInfoDisplay);
        viewModel.getWeightLogs().observe(getViewLifecycleOwner(), this::updateWeightChart);
    }

    // ==================== UPDATE UI ====================

    private void updateUserInfoDisplay(UserInfo info) {
        if (info == null) return;

        // Personal Info
        tvName.setText(info.getName());
        tvAge.setText(String.valueOf(info.getAge()));
        tvGender.setText(info.getGenderDisplay());
        tvHeight.setText(String.format(Locale.getDefault(), "%.0f cm", info.getHeight()));
        tvWeight.setText(String.format(Locale.getDefault(), "%.1f kg", info.getWeight()));

        // Goals
        tvActivityLevel.setText(info.getActivityLevelDisplay());
        tvWeightGoal.setText(info.getWeightGoalDisplay());
        tvCalorieGoal.setText(String.format(Locale.getDefault(), "%d kcal", info.getDailyCalorieGoal()));

        // BMI
        updateBMIDisplay(info);
    }

    private void updateBMIDisplay(UserInfo info) {
        float bmi = info.getBmi();
        String category = info.getBmiCategory();

        tvBMI.setText(String.format(Locale.getDefault(), "%.1f", bmi));
        chipBMICategory.setText(category);

        // Set color based on BMI category (Asian standard)
        int colorRes, bgColorRes;
        if (bmi < 18.5f) {
            colorRes = R.color.warning;
            bgColorRes = R.color.warning_container;
        } else if (bmi < 23f) {
            colorRes = R.color.success;
            bgColorRes = R.color.success_container;
        } else if (bmi < 25f) {
            colorRes = R.color.warning;
            bgColorRes = R.color.warning_container;
        } else if (bmi < 30f) {
            colorRes = R.color.secondary;
            bgColorRes = R.color.secondary_container;
        } else {
            colorRes = R.color.error;
            bgColorRes = R.color.error_container;
        }

        tvBMI.setTextColor(requireContext().getColor(colorRes));
        chipBMICategory.setTextColor(requireContext().getColor(colorRes));
        chipBMICategory.setChipBackgroundColorResource(bgColorRes);
    }

    private void updateWeightChart(List<WeightLog> logs) {
        if (logs == null || logs.size() < 2) {
            chartWeightTrend.setVisibility(View.GONE);
            tvEmptyChart.setVisibility(View.VISIBLE);
            return;
        }

        chartWeightTrend.setVisibility(View.VISIBLE);
        tvEmptyChart.setVisibility(View.GONE);

        // Create entries
        List<Entry> entries = new ArrayList<>();
        for (WeightLog log : logs) {
            entries.add(new Entry(log.getTimestamp(), log.getWeight()));
        }

        // Create dataset
        LineDataSet dataSet = new LineDataSet(entries, "Cân nặng");
        dataSet.setColor(requireContext().getColor(R.color.primary));
        dataSet.setCircleColor(requireContext().getColor(R.color.primary));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(requireContext().getColor(R.color.text_secondary));
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(requireContext().getColor(R.color.primary_container));
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Add target weight line
        float targetWeight = viewModel.getTargetWeight();
        if (targetWeight > 0) {
            LimitLine targetLine = new LimitLine(targetWeight, "Mục tiêu");
            targetLine.setLineWidth(1f);
            targetLine.setLineColor(requireContext().getColor(R.color.secondary));
            targetLine.enableDashedLine(10f, 10f, 0f);
            targetLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            targetLine.setTextSize(10f);
            targetLine.setTextColor(requireContext().getColor(R.color.secondary));

            chartWeightTrend.getAxisLeft().removeAllLimitLines();
            chartWeightTrend.getAxisLeft().addLimitLine(targetLine);
        }

        LineData lineData = new LineData(dataSet);
        chartWeightTrend.setData(lineData);
        chartWeightTrend.invalidate();
    }

    // ==================== DIALOGS ====================

    private void showEditPersonalInfoDialog() {
        EditPersonalInfoDialogFragment dialog = EditPersonalInfoDialogFragment.newInstance();
        dialog.show(getChildFragmentManager(), "EditPersonalInfo");
    }

    private void showEditGoalsDialog() {
        EditGoalsDialogFragment dialog = EditGoalsDialogFragment.newInstance();
        dialog.show(getChildFragmentManager(), "EditGoals");
    }

    private void showQuickWeightLogDialog() {
        QuickWeightLogDialogFragment dialog = QuickWeightLogDialogFragment.newInstance();
        dialog.show(getChildFragmentManager(), "QuickWeightLog");
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.confirm, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void performLogout() {
        viewModel.logout();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
