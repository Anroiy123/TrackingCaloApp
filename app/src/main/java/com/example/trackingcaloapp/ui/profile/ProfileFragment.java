package com.example.trackingcaloapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.utils.CalorieCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private TextInputEditText etName, etAge, etHeight, etWeight, etCalorieGoal;
    private Spinner spinnerGender, spinnerActivityLevel, spinnerWeightGoal;
    private TextView tvBMI;
    private Chip chipBMICategory;
    private MaterialButton btnCalculateGoal, btnSave;

    private UserPreferences userPreferences;

    private final String[] genders = {"Nam", "Nữ"};
    private final String[] activityLevels = {
            "Ít vận động",
            "Vận động nhẹ",
            "Vận động vừa",
            "Vận động nhiều",
            "Vận động rất nhiều"
    };
    private final String[] weightGoals = {"Giảm cân", "Giữ cân", "Tăng cân"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userPreferences = new UserPreferences(requireContext());

        initViews(view);
        setupSpinners();
        setupButtons();
        loadUserData();
    }


    private void initViews(View view) {
        etName = view.findViewById(R.id.etName);
        etAge = view.findViewById(R.id.etAge);
        etHeight = view.findViewById(R.id.etHeight);
        etWeight = view.findViewById(R.id.etWeight);
        etCalorieGoal = view.findViewById(R.id.etCalorieGoal);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerActivityLevel = view.findViewById(R.id.spinnerActivityLevel);
        spinnerWeightGoal = view.findViewById(R.id.spinnerWeightGoal);
        tvBMI = view.findViewById(R.id.tvBMI);
        chipBMICategory = view.findViewById(R.id.chipBMICategory);
        btnCalculateGoal = view.findViewById(R.id.btnCalculateGoal);
        btnSave = view.findViewById(R.id.btnSave);
    }

    private void setupSpinners() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, activityLevels);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityAdapter);

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, weightGoals);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeightGoal.setAdapter(goalAdapter);
    }

    private void setupButtons() {
        btnCalculateGoal.setOnClickListener(v -> calculateAndSetGoal());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        etName.setText(userPreferences.getUserName());
        etAge.setText(String.valueOf(userPreferences.getAge()));
        etHeight.setText(String.valueOf(userPreferences.getHeight()));
        etWeight.setText(String.valueOf(userPreferences.getWeight()));
        etCalorieGoal.setText(String.valueOf(userPreferences.getDailyCalorieGoal()));

        String gender = userPreferences.getGender();
        spinnerGender.setSelection(gender.equals("male") ? 0 : 1);

        int activityLevel = userPreferences.getActivityLevelPosition();
        spinnerActivityLevel.setSelection(Math.max(0, activityLevel - 1));

        spinnerWeightGoal.setSelection(userPreferences.getWeightGoalPosition());

        updateBMI();
    }

    private void updateBMI() {
        try {
            float height = Float.parseFloat(etHeight.getText().toString());
            float weight = Float.parseFloat(etWeight.getText().toString());

            float bmi = CalorieCalculator.calculateBMI(weight, height);
            String category = CalorieCalculator.getBMICategory(bmi);

            tvBMI.setText(String.format("%.1f", bmi));
            chipBMICategory.setText(category);

            // Set color based on BMI category (Asian standard)
            int colorRes, bgColorRes;
            if (bmi < 18.5f) {
                colorRes = R.color.warning;  // Thiếu cân
                bgColorRes = R.color.warning_container;
            } else if (bmi < 23f) {
                colorRes = R.color.success;  // Bình thường
                bgColorRes = R.color.success_container;
            } else if (bmi < 25f) {
                colorRes = R.color.warning;  // Thừa cân
                bgColorRes = R.color.warning_container;
            } else if (bmi < 30f) {
                colorRes = R.color.secondary;  // Tiền béo phì
                bgColorRes = R.color.secondary_container;
            } else {
                colorRes = R.color.error;  // Béo phì
                bgColorRes = R.color.error_container;
            }
            tvBMI.setTextColor(requireContext().getColor(colorRes));
            chipBMICategory.setTextColor(requireContext().getColor(colorRes));
            chipBMICategory.setChipBackgroundColorResource(bgColorRes);

        } catch (NumberFormatException e) {
            tvBMI.setText("--");
            chipBMICategory.setText("--");
        }
    }

    private void calculateAndSetGoal() {
        try {
            int age = Integer.parseInt(etAge.getText().toString());
            float height = Float.parseFloat(etHeight.getText().toString());
            float weight = Float.parseFloat(etWeight.getText().toString());
            boolean isMale = spinnerGender.getSelectedItemPosition() == 0;
            int activityLevel = spinnerActivityLevel.getSelectedItemPosition() + 1;
            int weightGoal = spinnerWeightGoal.getSelectedItemPosition();

            float activityMultiplier = getActivityMultiplier(activityLevel);
            int calorieGoal = CalorieCalculator.calculateDailyCalorieGoal(
                    weight, height, age, isMale, activityMultiplier, weightGoal);

            etCalorieGoal.setText(String.valueOf(calorieGoal));
            Toast.makeText(requireContext(), "Đã tính mục tiêu calo!", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }

    private float getActivityMultiplier(int level) {
        switch (level) {
            case 1: return 1.2f;
            case 2: return 1.375f;
            case 3: return 1.55f;
            case 4: return 1.725f;
            case 5: return 1.9f;
            default: return 1.55f;
        }
    }

    private void saveProfile() {
        try {
            String name = etName.getText().toString().trim();
            int age = Integer.parseInt(etAge.getText().toString());
            float height = Float.parseFloat(etHeight.getText().toString());
            float weight = Float.parseFloat(etWeight.getText().toString());
            int calorieGoal = Integer.parseInt(etCalorieGoal.getText().toString());
            boolean isMale = spinnerGender.getSelectedItemPosition() == 0;
            int activityLevel = spinnerActivityLevel.getSelectedItemPosition() + 1;
            int weightGoal = spinnerWeightGoal.getSelectedItemPosition();

            userPreferences.setUserName(name);
            userPreferences.setAge(age);
            userPreferences.setHeight(height);
            userPreferences.setWeight(weight);
            userPreferences.setGender(isMale ? "male" : "female");
            userPreferences.setActivityLevel(activityLevel);
            userPreferences.setWeightGoal(weightGoal);
            userPreferences.setDailyCalorieGoal(calorieGoal);

            Toast.makeText(requireContext(), "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
            updateBMI();

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}
