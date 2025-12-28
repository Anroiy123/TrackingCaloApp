package com.example.trackingcaloapp.ui.profile;

import android.os.Bundle;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

/**
 * Bottom Sheet Dialog để chỉnh sửa mục tiêu.
 */
public class EditGoalsDialogFragment extends BottomSheetDialogFragment {

    private ProfileViewModel viewModel;

    private AutoCompleteTextView spinnerActivityLevel, spinnerWeightGoal;
    private TextInputLayout tilCalorieGoal;
    private TextInputEditText etCalorieGoal;
    private MaterialButton btnCalculateGoal, btnCancel, btnSave, btnAdvancedGoal;
    private MaterialCardView cardTargetInfo;
    private TextView tvTargetInfo;

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

    private final String[] weightGoals = {"Giảm cân", "Giữ cân", "Tăng cân"};
    private final String[] weightGoalValues = {"lose", "maintain", "gain"};

    public static EditGoalsDialogFragment newInstance() {
        return new EditGoalsDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        initViews(view);
        setupSpinners();
        setupButtons();
        loadCurrentData();
    }

    private void initViews(View view) {
        spinnerActivityLevel = view.findViewById(R.id.spinnerActivityLevel);
        spinnerWeightGoal = view.findViewById(R.id.spinnerWeightGoal);
        tilCalorieGoal = view.findViewById(R.id.tilCalorieGoal);
        etCalorieGoal = view.findViewById(R.id.etCalorieGoal);
        btnCalculateGoal = view.findViewById(R.id.btnCalculateGoal);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);
        btnAdvancedGoal = view.findViewById(R.id.btnAdvancedGoal);
        cardTargetInfo = view.findViewById(R.id.cardTargetInfo);
        tvTargetInfo = view.findViewById(R.id.tvTargetInfo);
    }

    private void setupSpinners() {
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, activityLevels);
        spinnerActivityLevel.setAdapter(activityAdapter);

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, weightGoals);
        spinnerWeightGoal.setAdapter(goalAdapter);
    }

    private void setupButtons() {
        btnCalculateGoal.setOnClickListener(v -> calculateGoal());
        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveData());
        btnAdvancedGoal.setOnClickListener(v -> openAdvancedGoalDialog());
    }

    private void loadCurrentData() {
        UserInfo info = viewModel.getUserInfo().getValue();
        if (info != null) {
            spinnerActivityLevel.setText(info.getActivityLevelDisplay(), false);
            spinnerWeightGoal.setText(info.getWeightGoalDisplay(), false);
            etCalorieGoal.setText(String.valueOf(info.getDailyCalorieGoal()));
            
            // Show target info if set
            updateTargetInfoDisplay(info);
        }
    }
    
    private void updateTargetInfoDisplay(UserInfo info) {
        if (info.hasTargetWeight()) {
            cardTargetInfo.setVisibility(View.VISIBLE);
            int weeks = info.getDaysRemaining() / 7;
            String targetInfo = String.format(Locale.getDefault(), 
                    "%.1f kg trong %d tuần (%.2f kg/tuần)", 
                    info.getTargetWeight(), weeks, info.getWeeklyRate());
            tvTargetInfo.setText(targetInfo);
        } else {
            cardTargetInfo.setVisibility(View.GONE);
        }
    }
    
    private void openAdvancedGoalDialog() {
        dismiss();
        EditWeightGoalDialogFragment dialog = EditWeightGoalDialogFragment.newInstance();
        dialog.show(requireParentFragment().getChildFragmentManager(), "EditWeightGoalDialog");
    }

    private void calculateGoal() {
        // Nếu có target weight, tính theo target
        UserInfo info = viewModel.getUserInfo().getValue();
        if (info != null && info.hasTargetWeight()) {
            int daysRemaining = info.getDaysRemaining();
            if (daysRemaining > 0) {
                int calculatedGoal = viewModel.calculateCalorieGoalWithTarget(
                        info.getTargetWeight(), daysRemaining);
                etCalorieGoal.setText(String.valueOf(calculatedGoal));
                Toast.makeText(requireContext(), "Đã tính theo mục tiêu cân nặng!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // Fallback: tính theo cách cũ
        int calculatedGoal = viewModel.calculateCalorieGoal();
        etCalorieGoal.setText(String.valueOf(calculatedGoal));
        Toast.makeText(requireContext(), "Đã tính mục tiêu calo!", Toast.LENGTH_SHORT).show();
    }

    private void saveData() {
        tilCalorieGoal.setError(null);

        try {
            String activityLevel = getActivityLevelValue();
            String weightGoal = getWeightGoalValue();
            int calorieGoal = Integer.parseInt(etCalorieGoal.getText().toString().trim());

            // Disable buttons during save
            setButtonsEnabled(false);

            ValidationResult result = viewModel.saveGoals(activityLevel, weightGoal, calorieGoal);

            if (result.isValid()) {
                Toast.makeText(requireContext(), "Đã lưu mục tiêu!", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                setButtonsEnabled(true);
                if (result.hasError(ValidationResult.FIELD_CALORIE_GOAL)) {
                    tilCalorieGoal.setError(result.getError(ValidationResult.FIELD_CALORIE_GOAL));
                }
            }
        } catch (NumberFormatException e) {
            setButtonsEnabled(true);
            tilCalorieGoal.setError("Vui lòng nhập số hợp lệ");
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSave.setEnabled(enabled);
        btnCancel.setEnabled(enabled);
        btnCalculateGoal.setEnabled(enabled);
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

    private String getWeightGoalValue() {
        String selected = spinnerWeightGoal.getText().toString();
        for (int i = 0; i < weightGoals.length; i++) {
            if (weightGoals[i].equals(selected)) {
                return weightGoalValues[i];
            }
        }
        return "maintain";
    }
}
