package com.example.trackingcaloapp.model;

import com.example.trackingcaloapp.utils.CalorieCalculator;

/**
 * Model chứa thông tin người dùng cho Profile screen.
 * Bao gồm cả computed fields như BMI.
 */
public class UserInfo {
    private String name;
    private int age;
    private String gender;
    private float height;
    private float weight;
    private String activityLevel;
    private String weightGoal;
    private int dailyCalorieGoal;
    
    // Target weight goal fields
    private float targetWeight;
    private long targetDate;
    private float weeklyRate;

    public UserInfo() {}

    public UserInfo(String name, int age, String gender, float height, float weight,
                    String activityLevel, String weightGoal, int dailyCalorieGoal) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.weightGoal = weightGoal;
        this.dailyCalorieGoal = dailyCalorieGoal;
    }
    
    public UserInfo(String name, int age, String gender, float height, float weight,
                    String activityLevel, String weightGoal, int dailyCalorieGoal,
                    float targetWeight, long targetDate, float weeklyRate) {
        this(name, age, gender, height, weight, activityLevel, weightGoal, dailyCalorieGoal);
        this.targetWeight = targetWeight;
        this.targetDate = targetDate;
        this.weeklyRate = weeklyRate;
    }

    // Computed fields
    public float getBmi() {
        if (height <= 0 || weight <= 0) return 0;
        return CalorieCalculator.calculateBMI(weight, height);
    }

    public String getBmiCategory() {
        float bmi = getBmi();
        if (bmi <= 0) return "--";
        return CalorieCalculator.getBMICategory(bmi);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(String weightGoal) {
        this.weightGoal = weightGoal;
    }

    public int getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }

    public void setDailyCalorieGoal(int dailyCalorieGoal) {
        this.dailyCalorieGoal = dailyCalorieGoal;
    }

    // Helper methods for display
    public String getGenderDisplay() {
        if ("male".equals(gender)) return "Nam";
        if ("female".equals(gender)) return "Nữ";
        return gender;
    }

    public String getActivityLevelDisplay() {
        if (activityLevel == null) return "Vận động vừa";
        switch (activityLevel) {
            case "sedentary": return "Ít vận động";
            case "light": return "Vận động nhẹ";
            case "moderate": return "Vận động vừa";
            case "active": return "Vận động nhiều";
            case "very_active": return "Vận động rất nhiều";
            default: return activityLevel;
        }
    }

    public String getWeightGoalDisplay() {
        if (weightGoal == null) return "Giữ cân";
        switch (weightGoal) {
            case "lose": return "Giảm cân";
            case "maintain": return "Giữ cân";
            case "gain": return "Tăng cân";
            default: return weightGoal;
        }
    }
    
    // ==================== TARGET WEIGHT GETTERS/SETTERS ====================
    
    public float getTargetWeight() {
        return targetWeight;
    }
    
    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
    }
    
    public long getTargetDate() {
        return targetDate;
    }
    
    public void setTargetDate(long targetDate) {
        this.targetDate = targetDate;
    }
    
    public float getWeeklyRate() {
        return weeklyRate;
    }
    
    public void setWeeklyRate(float weeklyRate) {
        this.weeklyRate = weeklyRate;
    }
    
    /**
     * Kiểm tra có đặt mục tiêu cân nặng cụ thể không
     */
    public boolean hasTargetWeight() {
        return targetWeight > 0;
    }
    
    /**
     * Kiểm tra có đặt ngày mục tiêu không
     */
    public boolean hasTargetDate() {
        return targetDate > 0;
    }
    
    /**
     * Tính số ngày còn lại để đạt mục tiêu
     */
    public int getDaysRemaining() {
        if (!hasTargetDate()) return 0;
        long diff = targetDate - System.currentTimeMillis();
        return (int) (diff / (24L * 60L * 60L * 1000L));
    }
    
    /**
     * Tính số kg cần thay đổi
     */
    public float getWeightToChange() {
        if (!hasTargetWeight()) return 0;
        return Math.abs(weight - targetWeight);
    }
    
    /**
     * Kiểm tra đang giảm cân hay tăng cân
     */
    public boolean isLosingWeight() {
        return hasTargetWeight() && targetWeight < weight;
    }
}
