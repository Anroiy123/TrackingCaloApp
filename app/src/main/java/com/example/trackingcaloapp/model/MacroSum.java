package com.example.trackingcaloapp.model;

import androidx.room.Ignore;

/**
 * Model class cho aggregation query - tổng macro nutrients.
 * Dùng cho PieChart hiển thị phân bố protein/carbs/fat.
 */
public class MacroSum {

    private float protein;  // Tổng protein (g)
    private float carbs;    // Tổng carbs (g)
    private float fat;      // Tổng fat (g)

    // Constructor mặc định cho Room
    public MacroSum() {}

    @Ignore
    public MacroSum(float protein, float carbs, float fat) {
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    // Getters and Setters
    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    /**
     * Tính tổng macro (g)
     */
    public float getTotal() {
        return protein + carbs + fat;
    }

    /**
     * Tính % protein
     */
    public float getProteinPercent() {
        float total = getTotal();
        return total > 0 ? (protein / total) * 100 : 0;
    }

    /**
     * Tính % carbs
     */
    public float getCarbsPercent() {
        float total = getTotal();
        return total > 0 ? (carbs / total) * 100 : 0;
    }

    /**
     * Tính % fat
     */
    public float getFatPercent() {
        float total = getTotal();
        return total > 0 ? (fat / total) * 100 : 0;
    }

    /**
     * Tính tổng calories từ macro
     * Protein: 4 cal/g, Carbs: 4 cal/g, Fat: 9 cal/g
     */
    public float getTotalCalories() {
        return (protein * 4) + (carbs * 4) + (fat * 9);
    }
}
