package com.example.trackingcaloapp.model;

import androidx.room.Ignore;

/**
 * Model class cho aggregation query - tổng calo theo ngày.
 * Dùng cho LineChart hiển thị trend calories.
 */
public class DailyCalorieSum {

    private long dayTimestamp;      // Timestamp đầu ngày (00:00:00)
    private float totalCalories;    // Tổng calo trong ngày

    // Constructor mặc định cho Room
    public DailyCalorieSum() {}

    @Ignore
    public DailyCalorieSum(long dayTimestamp, float totalCalories) {
        this.dayTimestamp = dayTimestamp;
        this.totalCalories = totalCalories;
    }

    // Getters and Setters
    public long getDayTimestamp() {
        return dayTimestamp;
    }

    public void setDayTimestamp(long dayTimestamp) {
        this.dayTimestamp = dayTimestamp;
    }

    public float getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(float totalCalories) {
        this.totalCalories = totalCalories;
    }
}
