package com.example.trackingcaloapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.ui.addfood.AddFoodActivity;
import com.example.trackingcaloapp.ui.diary.DiaryActivity;
import com.example.trackingcaloapp.ui.main.MainActivity;
import com.example.trackingcaloapp.utils.CalorieCalculator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etAge, etHeight, etWeight, etCalorieGoal;
    private Spinner spinnerGender, spinnerActivityLevel, spinnerWeightGoal;
    private TextView tvBMI, tvBMICategory;
    private MaterialButton btnCalculateGoal, btnSave;
    private BottomNavigationView bottomNavigation;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userPreferences = new UserPreferences(this);

        initViews();
        setupSpinners();
        setupButtons();
        setupBottomNavigation();
        loadUserData();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etCalorieGoal = findViewById(R.id.etCalorieGoal);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        spinnerWeightGoal = findViewById(R.id.spinnerWeightGoal);
        tvBMI = findViewById(R.id.tvBMI);
        tvBMICategory = findViewById(R.id.tvBMICategory);
        btnCalculateGoal = findViewById(R.id.btnCalculateGoal);
        btnSave = findViewById(R.id.btnSave);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupSpinners() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, activityLevels);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityAdapter);

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, weightGoals);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeightGoal.setAdapter(goalAdapter);
    }

    private void setupButtons() {
        btnCalculateGoal.setOnClickListener(v -> calculateAndSetGoal());
        btnSave.setOnClickListener(v -> saveProfile());

        // Auto-update BMI when height or weight changes
        TextWatcher bmiWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateBMI();
            }
        };

        etHeight.addTextChangedListener(bmiWatcher);
        etWeight.addTextChangedListener(bmiWatcher);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_diary) {
                    startActivity(new Intent(ProfileActivity.this, DiaryActivity.class));
                    return true;
                } else if (itemId == R.id.nav_add) {
                    startActivity(new Intent(ProfileActivity.this, AddFoodActivity.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    return true;
                }
                return false;
            }
        });
    }

    private void loadUserData() {
        etName.setText(userPreferences.getUserName());
        etAge.setText(String.valueOf(userPreferences.getAge()));
        etHeight.setText(String.valueOf(userPreferences.getHeight()));
        etWeight.setText(String.valueOf(userPreferences.getWeight()));
        etCalorieGoal.setText(String.valueOf(userPreferences.getDailyCalorieGoal()));

        // Set gender spinner
        String gender = userPreferences.getGender();
        spinnerGender.setSelection(gender.equals("male") ? 0 : 1);

        // Set activity level spinner (1-5 to 0-4)
        int activityLevel = userPreferences.getActivityLevelPosition();
        spinnerActivityLevel.setSelection(Math.max(0, activityLevel - 1));

        // Set weight goal spinner
        spinnerWeightGoal.setSelection(userPreferences.getWeightGoalPosition());

        // Calculate and display BMI
        updateBMI();
    }

    private void updateBMI() {
        try {
            float height = Float.parseFloat(etHeight.getText().toString());
            float weight = Float.parseFloat(etWeight.getText().toString());

            float bmi = CalorieCalculator.calculateBMI(weight, height);
            String category = CalorieCalculator.getBMICategory(bmi);

            tvBMI.setText(String.format("%.1f", bmi));
            tvBMICategory.setText(category);

            // Set color based on BMI category
            int colorRes;
            if (bmi < 18.5f) {
                colorRes = R.color.warning;
            } else if (bmi < 25f) {
                colorRes = R.color.success;
            } else if (bmi < 30f) {
                colorRes = R.color.warning;
            } else {
                colorRes = R.color.error;
            }
            tvBMI.setTextColor(getColor(colorRes));
            tvBMICategory.setTextColor(getColor(colorRes));

        } catch (NumberFormatException e) {
            tvBMI.setText("--");
            tvBMICategory.setText("");
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
            Toast.makeText(this, "Đã tính mục tiêu calo!", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
            updateBMI();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}

