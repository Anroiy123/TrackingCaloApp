package com.example.trackingcaloapp.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trackingcaloapp.data.local.entity.WeightLog;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.data.repository.WeightLogRepository;
import com.example.trackingcaloapp.model.UserInfo;
import com.example.trackingcaloapp.model.ValidationResult;
import com.example.trackingcaloapp.utils.CalorieCalculator;

import java.util.List;

/**
 * ViewModel cho ProfileFragment.
 * Quản lý user info, weight logs, và validation.
 */
public class ProfileViewModel extends AndroidViewModel {

    private final UserPreferences userPreferences;
    private final WeightLogRepository weightLogRepository;

    private final MutableLiveData<UserInfo> userInfo = new MutableLiveData<>();
    private final LiveData<List<WeightLog>> weightLogs;
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userPreferences = new UserPreferences(application);
        weightLogRepository = new WeightLogRepository(application);
        weightLogs = weightLogRepository.getLast30DaysLogs();
        loadUserInfo();
    }

    // ==================== GETTERS ====================

    public LiveData<UserInfo> getUserInfo() {
        return userInfo;
    }

    public LiveData<List<WeightLog>> getWeightLogs() {
        return weightLogs;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    // ==================== LOAD DATA ====================

    public void loadUserInfo() {
        UserInfo info = new UserInfo(
                userPreferences.getUserName(),
                userPreferences.getAge(),
                userPreferences.getGender(),
                userPreferences.getHeight(),
                userPreferences.getWeight(),
                userPreferences.getActivityLevel(),
                userPreferences.getWeightGoal(),
                userPreferences.getDailyCalorieGoal(),
                userPreferences.getTargetWeight(),
                userPreferences.getTargetDate(),
                userPreferences.getWeeklyRate()
        );
        userInfo.setValue(info);
    }

    // ==================== SAVE DATA ====================

    public ValidationResult savePersonalInfo(String name, int age, String gender, 
                                              float height, float weight) {
        ValidationResult validation = validatePersonalInfo(name, age, height, weight);
        if (!validation.isValid()) {
            return validation;
        }

        // Check if weight changed to create log
        float oldWeight = userPreferences.getWeight();
        boolean weightChanged = Math.abs(oldWeight - weight) > 0.01f;

        // Save to preferences
        userPreferences.setUserName(name);
        userPreferences.setAge(age);
        userPreferences.setGender(gender);
        userPreferences.setHeight(height);
        userPreferences.setWeight(weight);

        // Create weight log if weight changed
        if (weightChanged) {
            WeightLog log = new WeightLog(weight, System.currentTimeMillis(), null);
            weightLogRepository.insert(log);
        }

        loadUserInfo();
        saveSuccess.setValue(true);
        return validation;
    }

    public ValidationResult saveGoals(String activityLevel, String weightGoal, int calorieGoal) {
        ValidationResult validation = validateGoals(calorieGoal);
        if (!validation.isValid()) {
            return validation;
        }

        userPreferences.setActivityLevel(activityLevel);
        userPreferences.setWeightGoal(weightGoal);
        userPreferences.setDailyCalorieGoal(calorieGoal);

        loadUserInfo();
        saveSuccess.setValue(true);
        return validation;
    }
    
    /**
     * Lưu mục tiêu với target weight và timeline
     */
    public ValidationResult saveGoalsWithTarget(String activityLevel, String weightGoal, 
            int calorieGoal, float targetWeight, long targetDate, float weeklyRate) {
        ValidationResult validation = validateGoalsWithTarget(calorieGoal, targetWeight, targetDate);
        if (!validation.isValid()) {
            return validation;
        }

        userPreferences.setActivityLevel(activityLevel);
        userPreferences.setWeightGoal(weightGoal);
        userPreferences.setDailyCalorieGoal(calorieGoal);
        userPreferences.setTargetWeight(targetWeight);
        userPreferences.setTargetDate(targetDate);
        userPreferences.setWeeklyRate(weeklyRate);

        loadUserInfo();
        saveSuccess.setValue(true);
        return validation;
    }
    
    /**
     * Xóa mục tiêu cân nặng cụ thể (quay về chế độ đơn giản)
     */
    public void clearWeightGoalTarget() {
        userPreferences.clearWeightGoalTarget();
        loadUserInfo();
    }

    public void logWeight(float weight, String note) {
        // Update current weight in preferences
        userPreferences.setWeight(weight);

        // Create weight log
        WeightLog log = new WeightLog(weight, System.currentTimeMillis(), note);
        weightLogRepository.insert(log);

        loadUserInfo();
        saveSuccess.setValue(true);
    }

    // ==================== VALIDATION ====================

    public ValidationResult validatePersonalInfo(String name, int age, float height, float weight) {
        ValidationResult result = new ValidationResult();

        // Name validation
        if (name == null || name.trim().isEmpty()) {
            result.addError(ValidationResult.FIELD_NAME, "Vui lòng nhập tên");
        }

        // Age validation
        if (age < 1 || age > 120) {
            result.addError(ValidationResult.FIELD_AGE, "Tuổi phải từ 1 đến 120");
        }

        // Height validation
        if (height < 50 || height > 250) {
            result.addError(ValidationResult.FIELD_HEIGHT, "Chiều cao phải từ 50 đến 250 cm");
        }

        // Weight validation
        if (weight < 20 || weight > 500) {
            result.addError(ValidationResult.FIELD_WEIGHT, "Cân nặng phải từ 20 đến 500 kg");
        }

        return result;
    }

    public ValidationResult validateGoals(int calorieGoal) {
        ValidationResult result = new ValidationResult();

        if (calorieGoal < 500 || calorieGoal > 10000) {
            result.addError(ValidationResult.FIELD_CALORIE_GOAL, 
                    "Mục tiêu calo phải từ 500 đến 10000");
        }

        return result;
    }
    
    /**
     * Validate mục tiêu với target weight
     */
    public ValidationResult validateGoalsWithTarget(int calorieGoal, float targetWeight, long targetDate) {
        ValidationResult result = validateGoals(calorieGoal);
        
        // Target weight validation
        if (targetWeight < 30 || targetWeight > 300) {
            result.addError("targetWeight", "Cân nặng mục tiêu phải từ 30 đến 300 kg");
        }
        
        // Target date validation
        if (targetDate > 0 && targetDate <= System.currentTimeMillis()) {
            result.addError("targetDate", "Ngày mục tiêu phải trong tương lai");
        }
        
        return result;
    }

    public ValidationResult validateWeight(float weight) {
        ValidationResult result = new ValidationResult();

        if (weight < 20 || weight > 500) {
            result.addError(ValidationResult.FIELD_WEIGHT, "Cân nặng phải từ 20 đến 500 kg");
        }

        return result;
    }

    // ==================== CALCULATIONS ====================

    public int calculateCalorieGoal() {
        UserInfo info = userInfo.getValue();
        if (info == null) return 2000;

        boolean isMale = "male".equals(info.getGender());
        float activityMultiplier = userPreferences.getActivityMultiplier();
        int weightGoalIndex = userPreferences.getWeightGoalPosition();

        return CalorieCalculator.calculateDailyCalorieGoal(
                info.getWeight(),
                info.getHeight(),
                info.getAge(),
                isMale,
                activityMultiplier,
                weightGoalIndex
        );
    }
    
    /**
     * Tính mục tiêu calo dựa trên target weight và timeline
     */
    public int calculateCalorieGoalWithTarget(float targetWeight, int daysToGoal) {
        UserInfo info = userInfo.getValue();
        if (info == null) return 2000;

        boolean isMale = "male".equals(info.getGender());
        float activityMultiplier = userPreferences.getActivityMultiplier();
        
        // Tính TDEE
        float bmr = CalorieCalculator.calculateBMR(
                info.getWeight(), info.getHeight(), info.getAge(), isMale);
        float tdee = CalorieCalculator.calculateTDEE(bmr, activityMultiplier);
        
        // Tính calorie goal với target
        return CalorieCalculator.calculateDailyCalorieGoalWithTarget(
                tdee, info.getWeight(), targetWeight, daysToGoal, isMale);
    }
    
    /**
     * Tính số ngày cần để đạt mục tiêu với tốc độ cho trước
     */
    public int calculateDaysToGoal(float targetWeight, float weeklyRate) {
        UserInfo info = userInfo.getValue();
        if (info == null) return 0;
        
        return CalorieCalculator.calculateDaysToGoal(info.getWeight(), targetWeight, weeklyRate);
    }
    
    /**
     * Tính tốc độ thay đổi cân nặng dựa trên timeline
     */
    public float calculateWeeklyRate(float targetWeight, int days) {
        UserInfo info = userInfo.getValue();
        if (info == null) return 0;
        
        return CalorieCalculator.calculateWeeklyRate(info.getWeight(), targetWeight, days);
    }
    
    /**
     * Lấy TDEE hiện tại
     */
    public float getTDEE() {
        UserInfo info = userInfo.getValue();
        if (info == null) return 2000;

        boolean isMale = "male".equals(info.getGender());
        float activityMultiplier = userPreferences.getActivityMultiplier();
        
        float bmr = CalorieCalculator.calculateBMR(
                info.getWeight(), info.getHeight(), info.getAge(), isMale);
        return CalorieCalculator.calculateTDEE(bmr, activityMultiplier);
    }

    public float getTargetWeight() {
        UserInfo info = userInfo.getValue();
        if (info == null) return 0;
        
        // Nếu đã set target weight cụ thể, trả về nó
        if (info.hasTargetWeight()) {
            return info.getTargetWeight();
        }

        // Fallback: tính dựa trên weight goal đơn giản
        float currentWeight = info.getWeight();
        String weightGoal = info.getWeightGoal();

        if ("lose".equals(weightGoal)) {
            return currentWeight * 0.9f;
        } else if ("gain".equals(weightGoal)) {
            return currentWeight * 1.1f;
        }
        return currentWeight;
    }

    // ==================== LOGOUT ====================

    public void logout() {
        userPreferences.logout();
    }
}
