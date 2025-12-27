package com.example.trackingcaloapp.model;

import androidx.room.Ignore;

/**
 * Model class cho aggregation query - tổng calo theo loại bữa ăn.
 * Dùng cho BarChart hiển thị so sánh bữa ăn.
 */
public class MealTypeCalories {

    private int mealType;           // 0=breakfast, 1=lunch, 2=dinner, 3=snack
    private float totalCalories;    // Tổng calo của loại bữa ăn

    // Constructor mặc định cho Room
    public MealTypeCalories() {}

    @Ignore
    public MealTypeCalories(int mealType, float totalCalories) {
        this.mealType = mealType;
        this.totalCalories = totalCalories;
    }

    // Getters and Setters
    public int getMealType() {
        return mealType;
    }

    public void setMealType(int mealType) {
        this.mealType = mealType;
    }

    public float getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(float totalCalories) {
        this.totalCalories = totalCalories;
    }

    /**
     * Lấy tên hiển thị của loại bữa ăn
     */
    public String getMealTypeName() {
        switch (mealType) {
            case 0:
                return "Sáng";
            case 1:
                return "Trưa";
            case 2:
                return "Tối";
            case 3:
                return "Phụ";
            default:
                return "Khác";
        }
    }
}
