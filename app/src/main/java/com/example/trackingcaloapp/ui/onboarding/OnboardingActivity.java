package com.example.trackingcaloapp.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.ui.main.MainActivity;
import com.example.trackingcaloapp.utils.CalorieCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class OnboardingActivity extends AppCompatActivity {

    private TextInputEditText etName, etAge, etHeight, etWeight;
    private RadioGroup rgGender;
    private Spinner spinnerActivityLevel, spinnerWeightGoal;
    private TextView tvCalculatedGoal;
    private MaterialButton btnFinish;

    private UserPreferences userPreferences;

    private final String[] activityLevels = {
            "Ít vận động (ngồi nhiều)",
            "Vận động nhẹ (1-3 ngày/tuần)",
            "Vận động vừa (3-5 ngày/tuần)",
            "Vận động nhiều (6-7 ngày/tuần)",
            "Vận động rất nhiều (2 lần/ngày)"
    };

    private final String[] weightGoals = {
            "Giảm cân",
            "Giữ cân",
            "Tăng cân"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        userPreferences = new UserPreferences(this);

        initViews();
        setupSpinners();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        rgGender = findViewById(R.id.rgGender);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        spinnerWeightGoal = findViewById(R.id.spinnerWeightGoal);
        tvCalculatedGoal = findViewById(R.id.tvCalculatedGoal);
        btnFinish = findViewById(R.id.btnFinish);
    }

    private void setupSpinners() {
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, activityLevels);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityAdapter);
        spinnerActivityLevel.setSelection(2); // Default: moderate

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, weightGoals);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeightGoal.setAdapter(goalAdapter);
        spinnerWeightGoal.setSelection(1); // Default: maintain
    }

    private void setupListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateAndDisplayGoal();
            }
        };

        etAge.addTextChangedListener(textWatcher);
        etHeight.addTextChangedListener(textWatcher);
        etWeight.addTextChangedListener(textWatcher);

        rgGender.setOnCheckedChangeListener((group, checkedId) -> calculateAndDisplayGoal());

        btnFinish.setOnClickListener(v -> saveAndFinish());
    }

    private void calculateAndDisplayGoal() {
        try {
            String ageStr = etAge.getText().toString();
            String heightStr = etHeight.getText().toString();
            String weightStr = etWeight.getText().toString();

            if (ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
                tvCalculatedGoal.setText("Nhập thông tin để tính mục tiêu calo");
                return;
            }

            int age = Integer.parseInt(ageStr);
            float height = Float.parseFloat(heightStr);
            float weight = Float.parseFloat(weightStr);
            boolean isMale = rgGender.getCheckedRadioButtonId() == R.id.rbMale;

            int activityLevel = spinnerActivityLevel.getSelectedItemPosition() + 1;
            int weightGoal = spinnerWeightGoal.getSelectedItemPosition();

            float activityMultiplier = getActivityMultiplier(activityLevel);
            int calorieGoal = CalorieCalculator.calculateDailyCalorieGoal(
                    weight, height, age, isMale, activityMultiplier, weightGoal);

            tvCalculatedGoal.setText("Mục tiêu calo: " + calorieGoal + " cal/ngày");
        } catch (NumberFormatException e) {
            tvCalculatedGoal.setText("Nhập thông tin hợp lệ");
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

    private void saveAndFinish() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            etName.setError("Vui lòng nhập tên");
            return;
        }
        if (ageStr.isEmpty()) {
            etAge.setError("Vui lòng nhập tuổi");
            return;
        }
        if (heightStr.isEmpty()) {
            etHeight.setError("Vui lòng nhập chiều cao");
            return;
        }
        if (weightStr.isEmpty()) {
            etWeight.setError("Vui lòng nhập cân nặng");
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            float height = Float.parseFloat(heightStr);
            float weight = Float.parseFloat(weightStr);
            boolean isMale = rgGender.getCheckedRadioButtonId() == R.id.rbMale;
            int activityLevel = spinnerActivityLevel.getSelectedItemPosition() + 1;
            int weightGoal = spinnerWeightGoal.getSelectedItemPosition();

            // Calculate calorie goal
            float activityMultiplier = getActivityMultiplier(activityLevel);
            int calorieGoal = CalorieCalculator.calculateDailyCalorieGoal(
                    weight, height, age, isMale, activityMultiplier, weightGoal);

            // Save to preferences
            userPreferences.setUserName(name);
            userPreferences.setAge(age);
            userPreferences.setHeight(height);
            userPreferences.setWeight(weight);
            userPreferences.setGender(isMale ? "male" : "female");
            userPreferences.setActivityLevel(activityLevel);
            userPreferences.setWeightGoal(weightGoal);
            userPreferences.setDailyCalorieGoal(calorieGoal);
            userPreferences.setOnboardingComplete(true);

            Toast.makeText(this, "Thiết lập hoàn tất!", Toast.LENGTH_SHORT).show();

            // Navigate to main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}

