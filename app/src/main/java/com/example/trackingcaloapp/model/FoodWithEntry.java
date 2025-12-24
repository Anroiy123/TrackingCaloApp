package com.example.trackingcaloapp.model;

import com.example.trackingcaloapp.data.local.entity.Food;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;

/**
 * Model class kết hợp Food và FoodEntry để hiển thị trong UI.
 * Dùng khi cần hiển thị thông tin đầy đủ của một mục nhập thực phẩm.
 */
public class FoodWithEntry {
    
    private Food food;
    private FoodEntry foodEntry;
    
    public FoodWithEntry(Food food, FoodEntry foodEntry) {
        this.food = food;
        this.foodEntry = foodEntry;
    }
    
    // ==================== GETTERS ====================
    
    public Food getFood() {
        return food;
    }
    
    public FoodEntry getFoodEntry() {
        return foodEntry;
    }
    
    // ==================== CONVENIENCE METHODS ====================
    
    /**
     * Lấy tên thực phẩm
     */
    public String getFoodName() {
        return food != null ? food.getName() : "";
    }
    
    /**
     * Lấy khối lượng đã ăn (gram)
     */
    public float getQuantity() {
        return foodEntry != null ? foodEntry.getQuantity() : 0;
    }
    
    /**
     * Lấy tổng calo
     */
    public float getTotalCalories() {
        return foodEntry != null ? foodEntry.getTotalCalories() : 0;
    }
    
    /**
     * Lấy loại bữa ăn
     */
    public int getMealType() {
        return foodEntry != null ? foodEntry.getMealType() : 0;
    }
    
    /**
     * Lấy tên hiển thị loại bữa ăn
     */
    public String getMealTypeDisplayName() {
        return foodEntry != null ? foodEntry.getMealTypeDisplayName() : "";
    }
    
    /**
     * Lấy category của thực phẩm
     */
    public String getCategory() {
        return food != null ? food.getCategory() : "";
    }
    
    /**
     * Lấy calo per 100g của thực phẩm
     */
    public float getCaloriesPer100g() {
        return food != null ? food.getCalories() : 0;
    }
    
    /**
     * Lấy timestamp
     */
    public long getDate() {
        return foodEntry != null ? foodEntry.getDate() : 0;
    }
    
    /**
     * Lấy ID của entry
     */
    public int getEntryId() {
        return foodEntry != null ? foodEntry.getId() : 0;
    }
    
    /**
     * Lấy ID của food
     */
    public int getFoodId() {
        return food != null ? food.getId() : 0;
    }
}

