package com.example.trackingcaloapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện cho thực phẩm yêu thích của user.
 * Dùng cho Quick Add feature - lưu các món user hay ăn.
 */
@Entity(
    tableName = "favorite_foods",
    foreignKeys = @ForeignKey(
        entity = Food.class,
        parentColumns = "id",
        childColumns = "foodId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = "foodId", unique = true)}
)
public class FavoriteFood {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int foodId;            // Foreign key liên kết với Food
    private float defaultQuantity; // Số lượng user hay ăn (gram)
    private long lastUsed;         // Timestamp lần dùng gần nhất (để sort)
    private int useCount;          // Đếm số lần dùng (để sort by popularity)

    // Constructor đầy đủ
    public FavoriteFood(int foodId, float defaultQuantity, long lastUsed, int useCount) {
        this.foodId = foodId;
        this.defaultQuantity = defaultQuantity;
        this.lastUsed = lastUsed;
        this.useCount = useCount;
    }

    // Constructor khi thêm mới (lastUsed = now, useCount = 1)
    @Ignore
    public FavoriteFood(int foodId, float defaultQuantity) {
        this(foodId, defaultQuantity, System.currentTimeMillis(), 1);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public float getDefaultQuantity() {
        return defaultQuantity;
    }

    public void setDefaultQuantity(float defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    /**
     * Increment use count và update lastUsed timestamp
     */
    public void incrementUsage() {
        this.useCount++;
        this.lastUsed = System.currentTimeMillis();
    }
}

