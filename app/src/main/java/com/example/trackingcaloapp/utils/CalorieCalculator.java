package com.example.trackingcaloapp.utils;

import com.example.trackingcaloapp.data.preferences.UserPreferences;

/**
 * Utility class để tính toán các chỉ số liên quan đến calo.
 * Sử dụng công thức Mifflin-St Jeor (được coi là chính xác nhất hiện nay).
 */
public class CalorieCalculator {
    
    /**
     * Tính BMR (Basal Metabolic Rate) - Tỷ lệ trao đổi chất cơ bản
     * Sử dụng công thức Mifflin-St Jeor
     * 
     * Nam: BMR = (10 × weight) + (6.25 × height) - (5 × age) + 5
     * Nữ: BMR = (10 × weight) + (6.25 × height) - (5 × age) - 161
     * 
     * @param weight Cân nặng (kg)
     * @param height Chiều cao (cm)
     * @param age Tuổi
     * @param isMale true nếu là nam, false nếu là nữ
     * @return BMR (calo/ngày)
     */
    public static float calculateBMR(float weight, float height, int age, boolean isMale) {
        float bmr = (10 * weight) + (6.25f * height) - (5 * age);
        if (isMale) {
            bmr += 5;
        } else {
            bmr -= 161;
        }
        return bmr;
    }
    
    /**
     * Tính TDEE (Total Daily Energy Expenditure) - Tổng năng lượng tiêu hao hàng ngày
     * TDEE = BMR × Activity Multiplier
     * 
     * @param bmr BMR đã tính
     * @param activityMultiplier Hệ số hoạt động (1.2 - 1.9)
     * @return TDEE (calo/ngày)
     */
    public static float calculateTDEE(float bmr, float activityMultiplier) {
        return bmr * activityMultiplier;
    }
    
    /**
     * Tính TDEE từ thông tin người dùng
     * 
     * @param weight Cân nặng (kg)
     * @param height Chiều cao (cm)
     * @param age Tuổi
     * @param isMale true nếu là nam
     * @param activityLevel Mức độ hoạt động
     * @return TDEE (calo/ngày)
     */
    public static float calculateTDEE(float weight, float height, int age, boolean isMale, String activityLevel) {
        float bmr = calculateBMR(weight, height, age, isMale);
        float multiplier = getActivityMultiplier(activityLevel);
        return calculateTDEE(bmr, multiplier);
    }
    
    /**
     * Tính mục tiêu calo hàng ngày dựa trên mục tiêu cân nặng
     * 
     * @param tdee TDEE đã tính
     * @param weightGoal Mục tiêu: "lose", "maintain", "gain"
     * @return Mục tiêu calo/ngày
     */
    public static int calculateDailyCalorieGoal(float tdee, String weightGoal) {
        switch (weightGoal) {
            case UserPreferences.GOAL_LOSE:
                // Giảm 500 calo/ngày để giảm ~0.5kg/tuần
                return Math.round(tdee - 500);
            case UserPreferences.GOAL_GAIN:
                // Tăng 500 calo/ngày để tăng ~0.5kg/tuần
                return Math.round(tdee + 500);
            case UserPreferences.GOAL_MAINTAIN:
            default:
                return Math.round(tdee);
        }
    }
    
    /**
     * Tính mục tiêu calo từ thông tin người dùng đầy đủ
     */
    public static int calculateDailyCalorieGoal(float weight, float height, int age,
                                                 boolean isMale, String activityLevel, String weightGoal) {
        float tdee = calculateTDEE(weight, height, age, isMale, activityLevel);
        return calculateDailyCalorieGoal(tdee, weightGoal);
    }

    /**
     * Tính mục tiêu calo từ thông tin người dùng đầy đủ (với activityMultiplier)
     */
    public static int calculateDailyCalorieGoal(float weight, float height, int age,
                                                 boolean isMale, float activityMultiplier, int weightGoal) {
        float bmr = calculateBMR(weight, height, age, isMale);
        float tdee = calculateTDEE(bmr, activityMultiplier);

        // Convert int weightGoal to String
        String weightGoalStr;
        switch (weightGoal) {
            case 0:
                weightGoalStr = UserPreferences.GOAL_LOSE;
                break;
            case 1:
                weightGoalStr = UserPreferences.GOAL_MAINTAIN;
                break;
            case 2:
                weightGoalStr = UserPreferences.GOAL_GAIN;
                break;
            default:
                weightGoalStr = UserPreferences.GOAL_MAINTAIN;
        }

        return calculateDailyCalorieGoal(tdee, weightGoalStr);
    }
    
    /**
     * Lấy hệ số hoạt động từ mức độ hoạt động
     */
    public static float getActivityMultiplier(String activityLevel) {
        switch (activityLevel) {
            case UserPreferences.ACTIVITY_SEDENTARY:
                return 1.2f;      // Ít vận động (ngồi văn phòng)
            case UserPreferences.ACTIVITY_LIGHT:
                return 1.375f;    // Vận động nhẹ (1-3 ngày/tuần)
            case UserPreferences.ACTIVITY_MODERATE:
                return 1.55f;     // Vận động vừa (3-5 ngày/tuần)
            case UserPreferences.ACTIVITY_ACTIVE:
                return 1.725f;    // Vận động nhiều (6-7 ngày/tuần)
            case UserPreferences.ACTIVITY_VERY_ACTIVE:
                return 1.9f;      // Vận động rất nhiều (2 lần/ngày)
            default:
                return 1.55f;
        }
    }
    
    /**
     * Tính BMI (Body Mass Index)
     * BMI = weight / (height in meters)^2
     * 
     * @param weight Cân nặng (kg)
     * @param height Chiều cao (cm)
     * @return BMI
     */
    public static float calculateBMI(float weight, float height) {
        float heightInMeters = height / 100f;
        return weight / (heightInMeters * heightInMeters);
    }
    
    /**
     * Lấy phân loại BMI theo tiêu chuẩn châu Á
     * Nguồn: WHO Expert Consultation 2004 - Appropriate BMI for Asian populations
     */
    public static String getBMICategory(float bmi) {
        if (bmi < 18.5f) {
            return "Thiếu cân";
        } else if (bmi < 23f) {
            return "Bình thường";
        } else if (bmi < 25f) {
            return "Thừa cân";
        } else if (bmi < 30f) {
            return "Tiền béo phì";
        } else if (bmi < 35f) {
            return "Béo phì độ I";
        } else if (bmi < 40f) {
            return "Béo phì độ II";
        } else {
            return "Béo phì độ III";
        }
    }
    
    /**
     * Tính calo NET (ăn vào - đốt cháy)
     */
    public static float calculateNetCalories(float caloriesConsumed, float caloriesBurned) {
        return caloriesConsumed - caloriesBurned;
    }
    
    /**
     * Tính calo còn lại có thể ăn trong ngày
     */
    public static float calculateRemainingCalories(int dailyGoal, float caloriesConsumed, float caloriesBurned) {
        float netCalories = calculateNetCalories(caloriesConsumed, caloriesBurned);
        return dailyGoal - netCalories;
    }
    
    /**
     * Tính phần trăm hoàn thành mục tiêu
     */
    public static int calculateProgressPercentage(int dailyGoal, float netCalories) {
        if (dailyGoal <= 0) return 0;
        return Math.round((netCalories / dailyGoal) * 100);
    }
    
    /**
     * Tính cân nặng lý tưởng (theo công thức Devine)
     * Nam: 50 + 2.3 × (height in inches - 60)
     * Nữ: 45.5 + 2.3 × (height in inches - 60)
     * 
     * @param height Chiều cao (cm)
     * @param isMale true nếu là nam
     * @return Cân nặng lý tưởng (kg)
     */
    public static float calculateIdealWeight(float height, boolean isMale) {
        float heightInInches = height / 2.54f;
        if (isMale) {
            return 50 + 2.3f * (heightInInches - 60);
        } else {
            return 45.5f + 2.3f * (heightInInches - 60);
        }
    }
}

