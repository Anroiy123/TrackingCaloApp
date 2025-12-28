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
    
    // ==================== WEIGHT GOAL CALCULATIONS ====================
    
    /**
     * Hằng số: 1kg mỡ cơ thể ≈ 7700 calories
     */
    public static final float CALORIES_PER_KG = 7700f;
    
    /**
     * Giới hạn an toàn cho calorie deficit/surplus mỗi ngày
     */
    public static final int MAX_DAILY_DEFICIT = 1000;  // Tối đa giảm 1000 cal/ngày
    public static final int MAX_DAILY_SURPLUS = 500;   // Tối đa tăng 500 cal/ngày
    public static final int MIN_CALORIES_MALE = 1500;  // Tối thiểu cho nam
    public static final int MIN_CALORIES_FEMALE = 1200; // Tối thiểu cho nữ
    
    /**
     * Tính calorie deficit/surplus mỗi ngày dựa trên mục tiêu cân nặng cụ thể
     * 
     * @param currentWeight Cân nặng hiện tại (kg)
     * @param targetWeight Cân nặng mục tiêu (kg)
     * @param daysToGoal Số ngày để đạt mục tiêu
     * @return Calorie deficit (âm) hoặc surplus (dương) mỗi ngày
     */
    public static int calculateDailyCalorieAdjustment(float currentWeight, float targetWeight, int daysToGoal) {
        if (daysToGoal <= 0) return 0;
        
        float weightDiff = currentWeight - targetWeight; // Dương = cần giảm, Âm = cần tăng
        float totalCalories = weightDiff * CALORIES_PER_KG;
        int dailyAdjustment = Math.round(totalCalories / daysToGoal);
        
        // Giới hạn an toàn
        if (dailyAdjustment > 0) {
            // Giảm cân: giới hạn deficit
            dailyAdjustment = Math.min(dailyAdjustment, MAX_DAILY_DEFICIT);
        } else {
            // Tăng cân: giới hạn surplus
            dailyAdjustment = Math.max(dailyAdjustment, -MAX_DAILY_SURPLUS);
        }
        
        return dailyAdjustment;
    }
    
    /**
     * Tính mục tiêu calo hàng ngày dựa trên mục tiêu cân nặng cụ thể
     * 
     * @param tdee TDEE đã tính
     * @param currentWeight Cân nặng hiện tại (kg)
     * @param targetWeight Cân nặng mục tiêu (kg)
     * @param daysToGoal Số ngày để đạt mục tiêu
     * @param isMale true nếu là nam (để áp dụng minimum calories)
     * @return Mục tiêu calo/ngày
     */
    public static int calculateDailyCalorieGoalWithTarget(float tdee, float currentWeight, 
            float targetWeight, int daysToGoal, boolean isMale) {
        int adjustment = calculateDailyCalorieAdjustment(currentWeight, targetWeight, daysToGoal);
        int goal = Math.round(tdee - adjustment);
        
        // Đảm bảo không dưới mức tối thiểu
        int minCalories = isMale ? MIN_CALORIES_MALE : MIN_CALORIES_FEMALE;
        return Math.max(goal, minCalories);
    }
    
    /**
     * Tính số ngày cần để đạt mục tiêu với tốc độ an toàn
     * 
     * @param currentWeight Cân nặng hiện tại (kg)
     * @param targetWeight Cân nặng mục tiêu (kg)
     * @param weeklyRate Tốc độ thay đổi mỗi tuần (kg/tuần), VD: 0.5
     * @return Số ngày cần thiết
     */
    public static int calculateDaysToGoal(float currentWeight, float targetWeight, float weeklyRate) {
        if (weeklyRate <= 0) return 0;
        float weightDiff = Math.abs(currentWeight - targetWeight);
        float weeks = weightDiff / weeklyRate;
        return Math.round(weeks * 7);
    }
    
    /**
     * Tính tốc độ thay đổi cân nặng mỗi tuần dựa trên timeline
     * 
     * @param currentWeight Cân nặng hiện tại (kg)
     * @param targetWeight Cân nặng mục tiêu (kg)
     * @param days Số ngày để đạt mục tiêu
     * @return Tốc độ thay đổi (kg/tuần)
     */
    public static float calculateWeeklyRate(float currentWeight, float targetWeight, int days) {
        if (days <= 0) return 0;
        float weightDiff = Math.abs(currentWeight - targetWeight);
        float weeks = days / 7f;
        return weightDiff / weeks;
    }
    
    /**
     * Kiểm tra tốc độ giảm/tăng cân có an toàn không
     * An toàn: Giảm 0.25-1kg/tuần, Tăng 0.25-0.5kg/tuần
     * 
     * @param weeklyRate Tốc độ (kg/tuần)
     * @param isLosing true nếu đang giảm cân
     * @return true nếu an toàn
     */
    public static boolean isWeeklyRateSafe(float weeklyRate, boolean isLosing) {
        if (weeklyRate < 0.1f) return true; // Quá chậm nhưng vẫn an toàn
        
        if (isLosing) {
            return weeklyRate <= 1.0f; // Tối đa 1kg/tuần khi giảm
        } else {
            return weeklyRate <= 0.5f; // Tối đa 0.5kg/tuần khi tăng
        }
    }
    
    /**
     * Lấy mô tả tốc độ thay đổi cân nặng
     */
    public static String getWeeklyRateDescription(float weeklyRate, boolean isLosing) {
        if (weeklyRate < 0.25f) {
            return "Rất chậm";
        } else if (weeklyRate <= 0.5f) {
            return "Chậm & An toàn";
        } else if (weeklyRate <= 0.75f) {
            return isLosing ? "Vừa phải" : "Nhanh";
        } else if (weeklyRate <= 1.0f) {
            return isLosing ? "Nhanh" : "Quá nhanh";
        } else {
            return "Quá nhanh - Không khuyến khích";
        }
    }
    
    /**
     * Tính ngày dự kiến đạt mục tiêu
     * 
     * @param currentWeight Cân nặng hiện tại (kg)
     * @param targetWeight Cân nặng mục tiêu (kg)
     * @param weeklyRate Tốc độ thay đổi (kg/tuần)
     * @return Timestamp của ngày dự kiến
     */
    public static long calculateTargetDate(float currentWeight, float targetWeight, float weeklyRate) {
        int days = calculateDaysToGoal(currentWeight, targetWeight, weeklyRate);
        return System.currentTimeMillis() + (days * 24L * 60L * 60L * 1000L);
    }
}

