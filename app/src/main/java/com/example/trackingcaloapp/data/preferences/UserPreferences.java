package com.example.trackingcaloapp.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class để quản lý SharedPreferences cho thông tin người dùng.
 * Lưu trữ các thông tin cá nhân và cài đặt ứng dụng.
 */
public class UserPreferences {
    
    private static final String PREF_NAME = "calorie_tracker_prefs";
    
    // Keys cho thông tin người dùng
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_AGE = "user_age";
    private static final String KEY_USER_GENDER = "user_gender";
    private static final String KEY_USER_HEIGHT = "user_height";
    private static final String KEY_USER_WEIGHT = "user_weight";
    private static final String KEY_ACTIVITY_LEVEL = "activity_level";
    private static final String KEY_DAILY_CALORIE_GOAL = "daily_calorie_goal";
    private static final String KEY_WEIGHT_GOAL = "weight_goal";
    
    // Keys cho mục tiêu cân nặng cụ thể
    private static final String KEY_TARGET_WEIGHT = "target_weight";
    private static final String KEY_TARGET_DATE = "target_date";
    private static final String KEY_WEEKLY_RATE = "weekly_rate";
    
    // Keys cho cài đặt app
    private static final String KEY_IS_ONBOARDING_COMPLETE = "is_onboarding_complete";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    // Activity levels
    public static final String ACTIVITY_SEDENTARY = "sedentary";           // Ít vận động
    public static final String ACTIVITY_LIGHT = "light";                   // Vận động nhẹ
    public static final String ACTIVITY_MODERATE = "moderate";             // Vận động vừa
    public static final String ACTIVITY_ACTIVE = "active";                 // Vận động nhiều
    public static final String ACTIVITY_VERY_ACTIVE = "very_active";       // Vận động rất nhiều
    
    // Weight goals
    public static final String GOAL_LOSE = "lose";                         // Giảm cân
    public static final String GOAL_MAINTAIN = "maintain";                 // Duy trì
    public static final String GOAL_GAIN = "gain";                         // Tăng cân
    
    // Gender
    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";
    
    private final SharedPreferences sharedPreferences;
    
    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    // ==================== USER INFO ====================
    
    public void setUserName(String name) {
        sharedPreferences.edit().putString(KEY_USER_NAME, name).apply();
    }
    
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }
    
    public void setUserAge(int age) {
        sharedPreferences.edit().putInt(KEY_USER_AGE, age).apply();
    }
    
    public int getUserAge() {
        return sharedPreferences.getInt(KEY_USER_AGE, 25);
    }
    
    public void setUserGender(String gender) {
        sharedPreferences.edit().putString(KEY_USER_GENDER, gender).apply();
    }
    
    public String getUserGender() {
        return sharedPreferences.getString(KEY_USER_GENDER, GENDER_MALE);
    }
    
    public void setUserHeight(float height) {
        sharedPreferences.edit().putFloat(KEY_USER_HEIGHT, height).apply();
    }
    
    public float getUserHeight() {
        return sharedPreferences.getFloat(KEY_USER_HEIGHT, 170f);
    }
    
    public void setUserWeight(float weight) {
        sharedPreferences.edit().putFloat(KEY_USER_WEIGHT, weight).apply();
    }
    
    public float getUserWeight() {
        return sharedPreferences.getFloat(KEY_USER_WEIGHT, 65f);
    }

    // Alias methods for compatibility
    public void setAge(int age) {
        setUserAge(age);
    }

    public int getAge() {
        return getUserAge();
    }

    public void setHeight(float height) {
        setUserHeight(height);
    }

    public float getHeight() {
        return getUserHeight();
    }

    public void setWeight(float weight) {
        setUserWeight(weight);
    }

    public float getWeight() {
        return getUserWeight();
    }

    public void setGender(String gender) {
        setUserGender(gender);
    }

    public String getGender() {
        return getUserGender();
    }

    public void setActivityLevel(String level) {
        sharedPreferences.edit().putString(KEY_ACTIVITY_LEVEL, level).apply();
    }

    // Overload to accept int (spinner position + 1)
    public void setActivityLevel(int level) {
        String levelStr;
        switch (level) {
            case 1:
                levelStr = ACTIVITY_SEDENTARY;
                break;
            case 2:
                levelStr = ACTIVITY_LIGHT;
                break;
            case 3:
                levelStr = ACTIVITY_MODERATE;
                break;
            case 4:
                levelStr = ACTIVITY_ACTIVE;
                break;
            case 5:
                levelStr = ACTIVITY_VERY_ACTIVE;
                break;
            default:
                levelStr = ACTIVITY_MODERATE;
        }
        setActivityLevel(levelStr);
    }

    public String getActivityLevel() {
        return sharedPreferences.getString(KEY_ACTIVITY_LEVEL, ACTIVITY_MODERATE);
    }

    public void setDailyCalorieGoal(int calories) {
        sharedPreferences.edit().putInt(KEY_DAILY_CALORIE_GOAL, calories).apply();
    }

    public int getDailyCalorieGoal() {
        return sharedPreferences.getInt(KEY_DAILY_CALORIE_GOAL, 2000);
    }

    public void setWeightGoal(String goal) {
        sharedPreferences.edit().putString(KEY_WEIGHT_GOAL, goal).apply();
    }

    // Overload to accept int (spinner position)
    public void setWeightGoal(int goalPosition) {
        String goalStr;
        switch (goalPosition) {
            case 0:
                goalStr = GOAL_LOSE;
                break;
            case 1:
                goalStr = GOAL_MAINTAIN;
                break;
            case 2:
                goalStr = GOAL_GAIN;
                break;
            default:
                goalStr = GOAL_MAINTAIN;
        }
        setWeightGoal(goalStr);
    }

    public String getWeightGoal() {
        return sharedPreferences.getString(KEY_WEIGHT_GOAL, GOAL_MAINTAIN);
    }

    // ==================== TARGET WEIGHT GOAL ====================
    
    /**
     * Lưu cân nặng mục tiêu (kg)
     */
    public void setTargetWeight(float weight) {
        sharedPreferences.edit().putFloat(KEY_TARGET_WEIGHT, weight).apply();
    }
    
    /**
     * Lấy cân nặng mục tiêu (kg). Trả về 0 nếu chưa set.
     */
    public float getTargetWeight() {
        return sharedPreferences.getFloat(KEY_TARGET_WEIGHT, 0f);
    }
    
    /**
     * Kiểm tra đã set target weight chưa
     */
    public boolean hasTargetWeight() {
        return getTargetWeight() > 0;
    }
    
    /**
     * Lưu ngày mục tiêu (timestamp)
     */
    public void setTargetDate(long timestamp) {
        sharedPreferences.edit().putLong(KEY_TARGET_DATE, timestamp).apply();
    }
    
    /**
     * Lấy ngày mục tiêu (timestamp). Trả về 0 nếu chưa set.
     */
    public long getTargetDate() {
        return sharedPreferences.getLong(KEY_TARGET_DATE, 0L);
    }
    
    /**
     * Kiểm tra đã set target date chưa
     */
    public boolean hasTargetDate() {
        return getTargetDate() > 0;
    }
    
    /**
     * Lưu tốc độ giảm/tăng cân mỗi tuần (kg/tuần)
     * Giá trị phổ biến: 0.25, 0.5, 0.75, 1.0
     */
    public void setWeeklyRate(float rate) {
        sharedPreferences.edit().putFloat(KEY_WEEKLY_RATE, rate).apply();
    }
    
    /**
     * Lấy tốc độ giảm/tăng cân mỗi tuần (kg/tuần). Mặc định 0.5kg/tuần.
     */
    public float getWeeklyRate() {
        return sharedPreferences.getFloat(KEY_WEEKLY_RATE, 0.5f);
    }
    
    /**
     * Xóa mục tiêu cân nặng (reset về chế độ đơn giản)
     */
    public void clearWeightGoalTarget() {
        sharedPreferences.edit()
            .remove(KEY_TARGET_WEIGHT)
            .remove(KEY_TARGET_DATE)
            .remove(KEY_WEEKLY_RATE)
            .apply();
    }

    // Get weight goal as int (spinner position)
    public int getWeightGoalPosition() {
        String goal = getWeightGoal();
        switch (goal) {
            case GOAL_LOSE:
                return 0;
            case GOAL_MAINTAIN:
                return 1;
            case GOAL_GAIN:
                return 2;
            default:
                return 1;
        }
    }

    // Get activity level as int (spinner position + 1)
    public int getActivityLevelPosition() {
        String level = getActivityLevel();
        switch (level) {
            case ACTIVITY_SEDENTARY:
                return 1;
            case ACTIVITY_LIGHT:
                return 2;
            case ACTIVITY_MODERATE:
                return 3;
            case ACTIVITY_ACTIVE:
                return 4;
            case ACTIVITY_VERY_ACTIVE:
                return 5;
            default:
                return 3;
        }
    }
    
    // ==================== APP SETTINGS ====================
    
    public void setOnboardingComplete(boolean complete) {
        sharedPreferences.edit().putBoolean(KEY_IS_ONBOARDING_COMPLETE, complete).apply();
    }
    
    public boolean isOnboardingComplete() {
        return sharedPreferences.getBoolean(KEY_IS_ONBOARDING_COMPLETE, false);
    }
    
    public void setThemeMode(String mode) {
        sharedPreferences.edit().putString(KEY_THEME_MODE, mode).apply();
    }
    
    public String getThemeMode() {
        return sharedPreferences.getString(KEY_THEME_MODE, "system");
    }
    
// ==================== LOGIN STATE ====================

    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_LOGIN_USERNAME = "login_username";

    public void setLoggedIn(boolean loggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_CURRENT_USER_ID)
            .remove(KEY_LOGIN_USERNAME)
            .apply();
    }

    public void setCurrentUserId(int userId) {
        sharedPreferences.edit().putInt(KEY_CURRENT_USER_ID, userId).apply();
    }

    public int getCurrentUserId() {
        return sharedPreferences.getInt(KEY_CURRENT_USER_ID, -1);
    }

    public void setLoginUsername(String username) {
        sharedPreferences.edit().putString(KEY_LOGIN_USERNAME, username).apply();
    }

    public String getLoginUsername() {
        return sharedPreferences.getString(KEY_LOGIN_USERNAME, "");
    }

    // ==================== HELPER METHODS ====================
    
    /**
     * Lấy tên hiển thị của mức độ hoạt động
     */
    public String getActivityLevelDisplayName() {
        switch (getActivityLevel()) {
            case ACTIVITY_SEDENTARY:
                return "Ít vận động";
            case ACTIVITY_LIGHT:
                return "Vận động nhẹ";
            case ACTIVITY_MODERATE:
                return "Vận động vừa";
            case ACTIVITY_ACTIVE:
                return "Vận động nhiều";
            case ACTIVITY_VERY_ACTIVE:
                return "Vận động rất nhiều";
            default:
                return "Vận động vừa";
        }
    }
    
    /**
     * Lấy tên hiển thị của mục tiêu cân nặng
     */
    public String getWeightGoalDisplayName() {
        switch (getWeightGoal()) {
            case GOAL_LOSE:
                return "Giảm cân";
            case GOAL_MAINTAIN:
                return "Duy trì";
            case GOAL_GAIN:
                return "Tăng cân";
            default:
                return "Duy trì";
        }
    }
    
    /**
     * Lấy hệ số hoạt động cho tính TDEE
     */
    public float getActivityMultiplier() {
        switch (getActivityLevel()) {
            case ACTIVITY_SEDENTARY:
                return 1.2f;
            case ACTIVITY_LIGHT:
                return 1.375f;
            case ACTIVITY_MODERATE:
                return 1.55f;
            case ACTIVITY_ACTIVE:
                return 1.725f;
            case ACTIVITY_VERY_ACTIVE:
                return 1.9f;
            default:
                return 1.55f;
        }
    }
    
    /**
     * Kiểm tra xem user đã nhập đủ thông tin chưa
     */
    public boolean hasUserInfo() {
        return !getUserName().isEmpty() && 
               getUserAge() > 0 && 
               getUserHeight() > 0 && 
               getUserWeight() > 0;
    }
    
    /**
     * Xóa tất cả dữ liệu user
     */
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }
}

