package com.example.trackingcaloapp.model;

import androidx.room.Embedded;
import androidx.room.Ignore;

import com.example.trackingcaloapp.data.local.entity.Food;

/**
 * Model class kết hợp Food với thông tin favorite.
 * Dùng để hiển thị star icon và thông tin favorite trên UI.
 */
public class FoodWithDetails {

    @Embedded
    private Food food;

    // Thông tin từ FavoriteFood (nullable nếu không phải favorite)
    private Integer favoriteId;
    private Float defaultQuantity;
    private Long lastUsed;
    private Integer useCount;
    private boolean isFavorite;

    // Default constructor for Room
    public FoodWithDetails() {}

    // Constructor đầy đủ
    @Ignore
    public FoodWithDetails(Food food, Integer favoriteId, Float defaultQuantity,
                           Long lastUsed, Integer useCount, boolean isFavorite) {
        this.food = food;
        this.favoriteId = favoriteId;
        this.defaultQuantity = defaultQuantity;
        this.lastUsed = lastUsed;
        this.useCount = useCount;
        this.isFavorite = isFavorite;
    }

    // Constructor từ Food (không phải favorite)
    @Ignore
    public FoodWithDetails(Food food) {
        this.food = food;
        this.isFavorite = false;
    }

    // Getters and Setters
    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public Integer getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Integer favoriteId) {
        this.favoriteId = favoriteId;
    }

    public Float getDefaultQuantity() {
        return defaultQuantity;
    }

    public void setDefaultQuantity(Float defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }

    public Long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // ==================== CONVENIENCE METHODS ====================

    /**
     * Lấy quantity mặc định (hoặc serving size nếu không có)
     */
    public float getQuantityToUse() {
        if (defaultQuantity != null && defaultQuantity > 0) {
            return defaultQuantity;
        }
        return food != null ? food.getServingSize() : 100f;
    }

    /**
     * Lấy ID của food
     */
    public int getFoodId() {
        return food != null ? food.getId() : 0;
    }

    /**
     * Lấy tên food
     */
    public String getFoodName() {
        return food != null ? food.getName() : "";
    }

    /**
     * Lấy calories của food
     */
    public float getCalories() {
        return food != null ? food.getCalories() : 0;
    }
}

