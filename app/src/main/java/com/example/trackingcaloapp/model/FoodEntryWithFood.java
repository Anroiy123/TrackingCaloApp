package com.example.trackingcaloapp.model;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.example.trackingcaloapp.data.local.entity.Food;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;

/**
 * Model class kết hợp FoodEntry với Food.
 * Dùng để hiển thị entry kèm tên món ăn và thông tin dinh dưỡng.
 */
public class FoodEntryWithFood {

    @Embedded
    private FoodEntry foodEntry;

    @Relation(
        parentColumn = "foodId",
        entityColumn = "id"
    )
    private Food food;

    // Default constructor for Room
    public FoodEntryWithFood() {}

    // Constructor đầy đủ
    @Ignore
    public FoodEntryWithFood(FoodEntry foodEntry, Food food) {
        this.foodEntry = foodEntry;
        this.food = food;
    }

    // Getters and Setters
    public FoodEntry getFoodEntry() {
        return foodEntry;
    }

    public void setFoodEntry(FoodEntry foodEntry) {
        this.foodEntry = foodEntry;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    // ==================== CONVENIENCE METHODS ====================

    /**
     * Lấy ID của entry
     */
    public int getEntryId() {
        return foodEntry != null ? foodEntry.getId() : 0;
    }

    /**
     * Lấy tên food
     */
    public String getFoodName() {
        return food != null ? food.getName() : "Unknown";
    }

    /**
     * Lấy category của food
     */
    public String getCategory() {
        return food != null ? food.getCategory() : "";
    }

    /**
     * Lấy meal type
     */
    public int getMealType() {
        return foodEntry != null ? foodEntry.getMealType() : 0;
    }

    /**
     * Lấy quantity (gram)
     */
    public float getQuantity() {
        return foodEntry != null ? foodEntry.getQuantity() : 0;
    }

    /**
     * Lấy total calories đã tính
     */
    public float getTotalCalories() {
        return foodEntry != null ? foodEntry.getTotalCalories() : 0;
    }

    /**
     * Lấy total protein đã tính
     */
    public float getTotalProtein() {
        return foodEntry != null ? foodEntry.getTotalProtein() : 0;
    }

    /**
     * Lấy total carbs đã tính
     */
    public float getTotalCarbs() {
        return foodEntry != null ? foodEntry.getTotalCarbs() : 0;
    }

    /**
     * Lấy total fat đã tính
     */
    public float getTotalFat() {
        return foodEntry != null ? foodEntry.getTotalFat() : 0;
    }

    /**
     * Lấy date timestamp
     */
    public long getDate() {
        return foodEntry != null ? foodEntry.getDate() : 0;
    }

    /**
     * Lấy serving unit của food
     */
    public String getServingUnit() {
        return food != null ? food.getServingUnit() : "g";
    }

    /**
     * Format quantity với unit
     */
    public String getFormattedQuantity() {
        if (foodEntry == null) return "0g";
        float qty = foodEntry.getQuantity();
        String unit = getServingUnit();
        if (qty == (int) qty) {
            return String.format("%d%s", (int) qty, unit);
        }
        return String.format("%.1f%s", qty, unit);
    }
}

