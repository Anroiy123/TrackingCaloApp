package com.example.trackingcaloapp.model;

/**
 * Model cho aggregation calo theo giờ (cho LineChart trong DiaryFragment).
 * Được sử dụng để hiển thị xu hướng ăn uống theo giờ trong ngày.
 */
public class HourlyCalorieSum {
    private int hour;           // 0-23
    private float totalCalories;

    public HourlyCalorieSum(int hour, float totalCalories) {
        this.hour = hour;
        this.totalCalories = totalCalories;
    }

    public int getHour() {
        return hour;
    }

    public float getTotalCalories() {
        return totalCalories;
    }
}
