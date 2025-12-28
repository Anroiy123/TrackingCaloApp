package com.example.trackingcaloapp.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OnboardingActivity extends AppCompatActivity {

    private TextInputEditText etName, etAge, etHeight, etWeight;
    private RadioGroup rgGender;
    private Spinner spinnerActivityLevel;
    private TextView tvCalculatedGoal;
    private MaterialButton btnFinish;

    // Target Weight Views
    private TextView tvCurrentWeightDisplay;
    private TextInputEditText etTargetWeight, etDuration;
    private TextInputLayout tilDuration;
    private ChipGroup chipGroupWeeklyRate;
    private Chip chipRate025, chipRate05, chipRate075, chipRate1;
    private MaterialCardView cardTargetInfo;
    private TextView tvCalculatedDuration, tvTargetDate, tvSafetyWarning;
    private CheckBox cbSkipTargetWeight;

    private UserPreferences userPreferences;

    // Target weight data
    private float selectedWeeklyRate = 0.5f;
    private boolean isCustomDuration = false;

    private final String[] activityLevels = {
            "Ít vận động (ngồi nhiều)",
            "Vận động nhẹ (1-3 ngày/tuần)",
            "Vận động vừa (3-5 ngày/tuần)",
            "Vận động nhiều (6-7 ngày/tuần)",
            "Vận động rất nhiều (2 lần/ngày)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        userPreferences = new UserPreferences(this);

        initViews();
        setupSpinners();
        setupTargetWeightSection();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        rgGender = findViewById(R.id.rgGender);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        tvCalculatedGoal = findViewById(R.id.tvCalculatedGoal);
        btnFinish = findViewById(R.id.btnFinish);

        // Target Weight Views
        tvCurrentWeightDisplay = findViewById(R.id.tvCurrentWeightDisplay);
        etTargetWeight = findViewById(R.id.etTargetWeight);
        etDuration = findViewById(R.id.etDuration);
        tilDuration = findViewById(R.id.tilDuration);
        chipGroupWeeklyRate = findViewById(R.id.chipGroupWeeklyRate);
        chipRate025 = findViewById(R.id.chipRate025);
        chipRate05 = findViewById(R.id.chipRate05);
        chipRate075 = findViewById(R.id.chipRate075);
        chipRate1 = findViewById(R.id.chipRate1);
        cardTargetInfo = findViewById(R.id.cardTargetInfo);
        tvCalculatedDuration = findViewById(R.id.tvCalculatedDuration);
        tvTargetDate = findViewById(R.id.tvTargetDate);
        tvSafetyWarning = findViewById(R.id.tvSafetyWarning);
        cbSkipTargetWeight = findViewById(R.id.cbSkipTargetWeight);
    }

    private void setupSpinners() {
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, activityLevels);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityAdapter);
        spinnerActivityLevel.setSelection(2); // Default: moderate
    }

    private void setupTargetWeightSection() {
        // Weekly rate chips
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
            calculateTargetWeightInfo();
        });

        // Target weight text watcher
        etTargetWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateTargetWeightInfo();
            }
        });

        // Duration text watcher
        etDuration.addTextChangedListener(new TextWatcher() {
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
                    calculateTargetWeightInfo();
                }
            }
        });

        // Skip checkbox
        cbSkipTargetWeight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setTargetWeightSectionEnabled(!isChecked);
            calculateAndDisplayGoal();
        });
    }

    private void setTargetWeightSectionEnabled(boolean enabled) {
        etTargetWeight.setEnabled(enabled);
        etDuration.setEnabled(enabled);
        chipRate025.setEnabled(enabled);
        chipRate05.setEnabled(enabled);
        chipRate075.setEnabled(enabled);
        chipRate1.setEnabled(enabled);

        if (!enabled) {
            cardTargetInfo.setVisibility(View.GONE);
        } else {
            calculateTargetWeightInfo();
        }
    }

    private void calculateTargetWeightInfo() {
        if (cbSkipTargetWeight.isChecked()) {
            cardTargetInfo.setVisibility(View.GONE);
            return;
        }

        String weightStr = etWeight.getText().toString().trim();
        String targetStr = etTargetWeight.getText().toString().trim();

        if (weightStr.isEmpty() || targetStr.isEmpty()) {
            cardTargetInfo.setVisibility(View.GONE);
            return;
        }

        try {
            float currentWeight = Float.parseFloat(weightStr);
            float targetWeight = Float.parseFloat(targetStr);

            if (currentWeight <= 0 || targetWeight <= 0) {
                cardTargetInfo.setVisibility(View.GONE);
                return;
            }

            float weightDiff = Math.abs(currentWeight - targetWeight);
            if (weightDiff < 0.1f) {
                // Maintain weight
                cardTargetInfo.setVisibility(View.GONE);
                return;
            }

            int daysToGoal;
            float weeklyRate;

            if (isCustomDuration) {
                String durationStr = etDuration.getText().toString().trim();
                if (durationStr.isEmpty()) {
                    cardTargetInfo.setVisibility(View.GONE);
                    return;
                }
                int weeks = Integer.parseInt(durationStr);
                daysToGoal = weeks * 7;
                weeklyRate = CalorieCalculator.calculateWeeklyRate(currentWeight, targetWeight, daysToGoal);
            } else {
                weeklyRate = selectedWeeklyRate;
                daysToGoal = CalorieCalculator.calculateDaysToGoal(currentWeight, targetWeight, weeklyRate);
            }

            // Calculate target date
            long targetDate = System.currentTimeMillis() + (daysToGoal * 24L * 60L * 60L * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Update UI
            cardTargetInfo.setVisibility(View.VISIBLE);
            int weeks = daysToGoal / 7;
            tvCalculatedDuration.setText(String.format(Locale.getDefault(), "%d tuần", weeks));
            tvTargetDate.setText(sdf.format(new Date(targetDate)));

            // Safety check
            boolean isLosing = targetWeight < currentWeight;
            boolean isSafe = CalorieCalculator.isWeeklyRateSafe(weeklyRate, isLosing);
            String rateDesc = CalorieCalculator.getWeeklyRateDescription(weeklyRate, isLosing);

            if (!isSafe) {
                tvSafetyWarning.setText("⚠️ " + rateDesc + " - Có thể không an toàn");
                tvSafetyWarning.setTextColor(getColor(R.color.error));
            } else {
                tvSafetyWarning.setText("✓ " + rateDesc);
                tvSafetyWarning.setTextColor(getColor(R.color.on_primary_container));
            }

            // Recalculate calorie goal
            calculateAndDisplayGoal();

        } catch (NumberFormatException e) {
            cardTargetInfo.setVisibility(View.GONE);
        }
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
                updateCurrentWeightDisplay();
            }
        };

        etAge.addTextChangedListener(textWatcher);
        etHeight.addTextChangedListener(textWatcher);
        etWeight.addTextChangedListener(textWatcher);

        rgGender.setOnCheckedChangeListener((group, checkedId) -> calculateAndDisplayGoal());

        btnFinish.setOnClickListener(v -> saveAndFinish());
    }

    private void updateCurrentWeightDisplay() {
        String weightStr = etWeight.getText().toString().trim();
        if (!weightStr.isEmpty()) {
            try {
                float weight = Float.parseFloat(weightStr);
                tvCurrentWeightDisplay.setText(String.format(Locale.getDefault(), "%.1f kg", weight));
            } catch (NumberFormatException e) {
                tvCurrentWeightDisplay.setText("-- kg");
            }
        } else {
            tvCurrentWeightDisplay.setText("-- kg");
        }
        calculateTargetWeightInfo();
    }

    private void calculateAndDisplayGoal() {
        try {
            String ageStr = etAge.getText().toString();
            String heightStr = etHeight.getText().toString();
            String weightStr = etWeight.getText().toString();

            if (ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
                tvCalculatedGoal.setText("--");
                return;
            }

            int age = Integer.parseInt(ageStr);
            float height = Float.parseFloat(heightStr);
            float weight = Float.parseFloat(weightStr);
            boolean isMale = rgGender.getCheckedRadioButtonId() == R.id.rbMale;

            int activityLevel = spinnerActivityLevel.getSelectedItemPosition() + 1;
            float activityMultiplier = getActivityMultiplier(activityLevel);

            int calorieGoal;

            // Check if using target weight or simple goal
            if (!cbSkipTargetWeight.isChecked()) {
                String targetStr = etTargetWeight.getText().toString().trim();
                if (!targetStr.isEmpty()) {
                    float targetWeight = Float.parseFloat(targetStr);
                    float weightDiff = Math.abs(weight - targetWeight);

                    if (weightDiff >= 0.1f) {
                        int daysToGoal;
                        if (isCustomDuration) {
                            String durationStr = etDuration.getText().toString().trim();
                            if (!durationStr.isEmpty()) {
                                int weeks = Integer.parseInt(durationStr);
                                daysToGoal = weeks * 7;
                            } else {
                                daysToGoal = CalorieCalculator.calculateDaysToGoal(weight, targetWeight, selectedWeeklyRate);
                            }
                        } else {
                            daysToGoal = CalorieCalculator.calculateDaysToGoal(weight, targetWeight, selectedWeeklyRate);
                        }

                        float bmr = CalorieCalculator.calculateBMR(weight, height, age, isMale);
                        float tdee = CalorieCalculator.calculateTDEE(bmr, activityMultiplier);
                        calorieGoal = CalorieCalculator.calculateDailyCalorieGoalWithTarget(
                                tdee, weight, targetWeight, daysToGoal, isMale);

                        tvCalculatedGoal.setText(String.valueOf(calorieGoal));
                        return;
                    }
                }
            }

            // Simple goal: maintain weight
            calorieGoal = CalorieCalculator.calculateDailyCalorieGoal(
                    weight, height, age, isMale, activityMultiplier, 1); // 1 = maintain

            tvCalculatedGoal.setText(String.valueOf(calorieGoal));
        } catch (NumberFormatException e) {
            tvCalculatedGoal.setText("--");
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

            float activityMultiplier = getActivityMultiplier(activityLevel);
            int calorieGoal;
            int weightGoalType; // 0=lose, 1=maintain, 2=gain

            // Save basic info
            userPreferences.setUserName(name);
            userPreferences.setAge(age);
            userPreferences.setHeight(height);
            userPreferences.setWeight(weight);
            userPreferences.setGender(isMale ? "male" : "female");
            userPreferences.setActivityLevel(activityLevel);

            // Check if using target weight
            if (!cbSkipTargetWeight.isChecked()) {
                String targetStr = etTargetWeight.getText().toString().trim();
                if (!targetStr.isEmpty()) {
                    float targetWeight = Float.parseFloat(targetStr);

                    // Validate target weight
                    if (targetWeight < 30 || targetWeight > 300) {
                        etTargetWeight.setError("Cân nặng mục tiêu phải từ 30 đến 300 kg");
                        return;
                    }

                    float weightDiff = Math.abs(weight - targetWeight);

                    if (weightDiff >= 0.1f) {
                        // Calculate days and weekly rate
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
                            weeklyRate = CalorieCalculator.calculateWeeklyRate(weight, targetWeight, daysToGoal);
                        } else {
                            weeklyRate = selectedWeeklyRate;
                            daysToGoal = CalorieCalculator.calculateDaysToGoal(weight, targetWeight, weeklyRate);
                        }

                        long targetDate = System.currentTimeMillis() + (daysToGoal * 24L * 60L * 60L * 1000L);

                        // Calculate calorie goal with target
                        float bmr = CalorieCalculator.calculateBMR(weight, height, age, isMale);
                        float tdee = CalorieCalculator.calculateTDEE(bmr, activityMultiplier);
                        calorieGoal = CalorieCalculator.calculateDailyCalorieGoalWithTarget(
                                tdee, weight, targetWeight, daysToGoal, isMale);

                        // Determine weight goal type
                        if (targetWeight < weight) {
                            weightGoalType = 0; // lose
                        } else {
                            weightGoalType = 2; // gain
                        }

                        // Save target weight info
                        userPreferences.setTargetWeight(targetWeight);
                        userPreferences.setTargetDate(targetDate);
                        userPreferences.setWeeklyRate(weeklyRate);
                        userPreferences.setWeightGoal(weightGoalType);
                        userPreferences.setDailyCalorieGoal(calorieGoal);
                        userPreferences.setOnboardingComplete(true);

                        Toast.makeText(this, "Thiết lập hoàn tất!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                        return;
                    }
                }
            }

            // Simple goal: maintain weight (no target weight)
            weightGoalType = 1; // maintain
            calorieGoal = CalorieCalculator.calculateDailyCalorieGoal(
                    weight, height, age, isMale, activityMultiplier, weightGoalType);

            userPreferences.setWeightGoal(weightGoalType);
            userPreferences.setDailyCalorieGoal(calorieGoal);
            userPreferences.clearWeightGoalTarget(); // Clear any existing target
            userPreferences.setOnboardingComplete(true);

            Toast.makeText(this, "Thiết lập hoàn tất!", Toast.LENGTH_SHORT).show();
            navigateToMain();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

