package com.example.trackingcaloapp.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.model.UserInfo;
import com.example.trackingcaloapp.model.ValidationResult;
import com.example.trackingcaloapp.utils.CalorieCalculator;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Dialog để đặt mục tiêu cân nặng với target weight và timeline.
 */
public class EditWeightGoalDialogFragment extends BottomSheetDialogFragment {

    private ProfileViewModel viewModel;

    // Views
    private TextView tvCurrentWeight, tvTargetWeightPreview;
    private AutoCompleteTextView spinnerActivityLevel;
    private TextInputLayout tilTargetWeight, tilDuration;
    private TextInputEditText etTargetWeight, etDuration;
    private ChipGroup chipGroupWeeklyRate;
    private Chip chipRate025, chipRate05, chipRate075, chipRate1;
    private MaterialCardView cardCalculatedInfo;
    private TextView tvCalculatedCalories, tvCalculatedDuration, tvTargetDate, tvSafetyWarning;
    private MaterialButton btnCancel, btnSave;

    // Data
    private float currentWeight = 0f;
    private float selectedWeeklyRate = 0.5f;
    private boolean isCustomDuration = false;

    private final String[] activityLevels = {
            "Ít vận động",
            "Vận động nhẹ",
            "Vận động vừa",
            "Vận động nhiều",
            "Vận động rất nhiều"
    };
    private final String[] activityLevelValues = {
            "sedentary", "light", "moderate", "active", "very_active"
    };

    public static EditWeightGoalDialogFragment newInstance() {
        return new EditWeightGoalDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_weight_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        initViews(view);
        setupSpinners();
        setupChips();
        setupTextWatchers();
        setupButtons();
        loadCurrentData();
    }

    private void initViews(View view) {
        tvCurrentWeight = view.findViewById(R.id.tvCurrentWeight);
        tvTargetWeightPreview = view.findViewById(R.id.tvTargetWeightPreview);
        spinnerActivityLevel = view.findViewById(R.id.spinnerActivityLevel);
        tilTargetWeight = view.findViewById(R.id.tilTargetWeight);
        etTargetWeight = view.findViewById(R.id.etTargetWeight);
        tilDuration = view.findViewById(R.id.tilDuration);
        etDuration = view.findViewById(R.id.etDuration);
        chipGroupWeeklyRate = view.findViewById(R.id.chipGroupWeeklyRate);
        chipRate025 = view.findViewById(R.id.chipRate025);
        chipRate05 = view.findViewById(R.id.chipRate05);
        chipRate075 = view.findViewById(R.id.chipRate075);
        chipRate1 = view.findViewById(R.id.chipRate1);
        cardCalculatedInfo = view.findViewById(R.id.cardCalculatedInfo);
        tvCalculatedCalories = view.findViewById(R.id.tvCalculatedCalories);
        tvCalculatedDuration = view.findViewById(R.id.tvCalculatedDuration);
        tvTargetDate = view.findViewById(R.id.tvTargetDate);
        tvSafetyWarning = view.findViewById(R.id.tvSafetyWarning);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);
    }

    private void setupSpinners() {
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, activityLevels);
        spinnerActivityLevel.setAdapter(activityAdapter);
    }

    private void setupChips() {
        chipGroupWeeklyRate.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipRate025) {
                selectedWeeklyRate = 0.25f;
            } else if (checkedId == R.id.chipRate05) {
                selectedWeeklyRate = 0.5f;
            } else if (checkedId == R.id.chipRate075) {
                selectedWeeklyRate = 0.75f;
            } else if (checkedId == R.id.chipRate1) {
                selectedWeeklyRate = 1.0f;
            }
            
            isCustomDuration = false;
            etDuration.setText("");
            calculateAndDisplay();
        });
    }


    private void setupTextWatchers() {
        TextWatcher targetWeightWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateTargetWeightPreview();
                calculateAndDisplay();
            }
        };
        etTargetWeight.addTextChangedListener(targetWeightWatcher);

        TextWatcher durationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (!text.isEmpty()) {
                    isCustomDuration = true;
                    chipGroupWeeklyRate.clearCheck();
                    calculateAndDisplay();
                }
            }
        };
        etDuration.addTextChangedListener(durationWatcher);
    }

    private void setupButtons() {
        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveData());
    }

    private void loadCurrentData() {
        UserInfo info = viewModel.getUserInfo().getValue();
        if (info != null) {
            currentWeight = info.getWeight();
            tvCurrentWeight.setText(String.format(Locale.getDefault(), "%.1f kg", currentWeight));
            spinnerActivityLevel.setText(info.getActivityLevelDisplay(), false);

            // Load existing target if any
            if (info.hasTargetWeight()) {
                etTargetWeight.setText(String.valueOf(info.getTargetWeight()));
                selectedWeeklyRate = info.getWeeklyRate();
                selectWeeklyRateChip(selectedWeeklyRate);
            }
        }
    }

    private void selectWeeklyRateChip(float rate) {
        if (Math.abs(rate - 0.25f) < 0.01f) {
            chipRate025.setChecked(true);
        } else if (Math.abs(rate - 0.5f) < 0.01f) {
            chipRate05.setChecked(true);
        } else if (Math.abs(rate - 0.75f) < 0.01f) {
            chipRate075.setChecked(true);
        } else if (Math.abs(rate - 1.0f) < 0.01f) {
            chipRate1.setChecked(true);
        }
    }

    private void updateTargetWeightPreview() {
        String targetStr = etTargetWeight.getText().toString().trim();
        if (targetStr.isEmpty()) {
            tvTargetWeightPreview.setText("-- kg");
            return;
        }

        try {
            float target = Float.parseFloat(targetStr);
            tvTargetWeightPreview.setText(String.format(Locale.getDefault(), "%.1f kg", target));
        } catch (NumberFormatException e) {
            tvTargetWeightPreview.setText("-- kg");
        }
    }

    private void calculateAndDisplay() {
        String targetStr = etTargetWeight.getText().toString().trim();
        if (targetStr.isEmpty()) {
            cardCalculatedInfo.setVisibility(View.GONE);
            return;
        }

        try {
            float targetWeight = Float.parseFloat(targetStr);
            if (targetWeight <= 0 || currentWeight <= 0) {
                cardCalculatedInfo.setVisibility(View.GONE);
                return;
            }

            float weightDiff = Math.abs(currentWeight - targetWeight);
            if (weightDiff < 0.1f) {
                // Maintain weight
                cardCalculatedInfo.setVisibility(View.GONE);
                return;
            }

            int daysToGoal;
            float weeklyRate;

            if (isCustomDuration) {
                String durationStr = etDuration.getText().toString().trim();
                if (durationStr.isEmpty()) {
                    cardCalculatedInfo.setVisibility(View.GONE);
                    return;
                }
                int weeks = Integer.parseInt(durationStr);
                daysToGoal = weeks * 7;
                weeklyRate = CalorieCalculator.calculateWeeklyRate(currentWeight, targetWeight, daysToGoal);
            } else {
                weeklyRate = selectedWeeklyRate;
                daysToGoal = CalorieCalculator.calculateDaysToGoal(currentWeight, targetWeight, weeklyRate);
            }

            // Calculate calorie goal
            int calorieGoal = viewModel.calculateCalorieGoalWithTarget(targetWeight, daysToGoal);

            // Calculate target date
            long targetDate = System.currentTimeMillis() + (daysToGoal * 24L * 60L * 60L * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Update UI
            cardCalculatedInfo.setVisibility(View.VISIBLE);
            tvCalculatedCalories.setText(String.format(Locale.getDefault(), "%d kcal", calorieGoal));
            
            int weeks = daysToGoal / 7;
            tvCalculatedDuration.setText(String.format(Locale.getDefault(), "%d tuần", weeks));
            tvTargetDate.setText(sdf.format(new Date(targetDate)));

            // Safety check
            boolean isLosing = targetWeight < currentWeight;
            boolean isSafe = CalorieCalculator.isWeeklyRateSafe(weeklyRate, isLosing);
            String rateDesc = CalorieCalculator.getWeeklyRateDescription(weeklyRate, isLosing);

            if (!isSafe) {
                tvSafetyWarning.setVisibility(View.VISIBLE);
                tvSafetyWarning.setText("⚠️ " + rateDesc + " - Có thể không an toàn");
                tvSafetyWarning.setTextColor(requireContext().getColor(R.color.error));
            } else {
                tvSafetyWarning.setVisibility(View.VISIBLE);
                tvSafetyWarning.setText("✓ " + rateDesc);
                tvSafetyWarning.setTextColor(requireContext().getColor(R.color.on_primary_container));
            }

        } catch (NumberFormatException e) {
            cardCalculatedInfo.setVisibility(View.GONE);
        }
    }

    private void saveData() {
        tilTargetWeight.setError(null);

        String targetStr = etTargetWeight.getText().toString().trim();
        if (targetStr.isEmpty()) {
            tilTargetWeight.setError("Vui lòng nhập cân nặng mục tiêu");
            return;
        }

        try {
            float targetWeight = Float.parseFloat(targetStr);
            
            // Validate
            if (targetWeight < 30 || targetWeight > 300) {
                tilTargetWeight.setError("Cân nặng phải từ 30 đến 300 kg");
                return;
            }

            // Calculate values
            int daysToGoal;
            float weeklyRate;

            if (isCustomDuration) {
                String durationStr = etDuration.getText().toString().trim();
                if (durationStr.isEmpty()) {
                    tilDuration.setError("Vui lòng nhập số tuần");
                    return;
                }
                int weeks = Integer.parseInt(durationStr);
                if (weeks < 1 || weeks > 104) {
                    tilDuration.setError("Số tuần phải từ 1 đến 104");
                    return;
                }
                daysToGoal = weeks * 7;
                weeklyRate = CalorieCalculator.calculateWeeklyRate(currentWeight, targetWeight, daysToGoal);
            } else {
                weeklyRate = selectedWeeklyRate;
                daysToGoal = CalorieCalculator.calculateDaysToGoal(currentWeight, targetWeight, weeklyRate);
            }

            long targetDate = System.currentTimeMillis() + (daysToGoal * 24L * 60L * 60L * 1000L);
            int calorieGoal = viewModel.calculateCalorieGoalWithTarget(targetWeight, daysToGoal);

            // Determine weight goal type
            String weightGoal;
            if (targetWeight < currentWeight) {
                weightGoal = "lose";
            } else if (targetWeight > currentWeight) {
                weightGoal = "gain";
            } else {
                weightGoal = "maintain";
            }

            String activityLevel = getActivityLevelValue();

            // Disable buttons during save
            setButtonsEnabled(false);

            ValidationResult result = viewModel.saveGoalsWithTarget(
                    activityLevel, weightGoal, calorieGoal,
                    targetWeight, targetDate, weeklyRate);

            if (result.isValid()) {
                Toast.makeText(requireContext(), "Đã lưu mục tiêu!", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                setButtonsEnabled(true);
                if (result.hasError("targetWeight")) {
                    tilTargetWeight.setError(result.getError("targetWeight"));
                }
            }

        } catch (NumberFormatException e) {
            tilTargetWeight.setError("Vui lòng nhập số hợp lệ");
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSave.setEnabled(enabled);
        btnCancel.setEnabled(enabled);
    }

    private String getActivityLevelValue() {
        String selected = spinnerActivityLevel.getText().toString();
        for (int i = 0; i < activityLevels.length; i++) {
            if (activityLevels[i].equals(selected)) {
                return activityLevelValues[i];
            }
        }
        return "moderate";
    }
}
