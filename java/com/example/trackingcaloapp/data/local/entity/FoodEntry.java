package com.example.trackingcaloapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện cho một mục nhập thực phẩm trong nhật ký ăn uống.
 * Liên kết với Food entity thông qua foodId.
 */
@Entity(
    tableName = "food_entries",
    foreignKeys = @ForeignKey(
        entity = Food.class,
        parentColumns = "id",
        childColumns = "foodId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("foodId"), @Index("date")}
)
public class FoodEntry {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int foodId;            // Foreign key liên kết với Food
    private float quantity;        // Khối lượng (gram)
    private int mealType;          // 0=breakfast, 1=lunch, 2=dinner, 3=snack
    private long date;             // Timestamp (milliseconds) - ngày ăn
    private float totalCalories;   // Calo đã tính = (food.calories * quantity) / 100
    private float totalProtein;    // Protein đã tính
    private float totalCarbs;      // Carbs đã tính
    private float totalFat;        // Fat đã tính

    // Default constructor for Room
    public FoodEntry() {}

    // Constructor
    @Ignore
    public FoodEntry(int foodId, float quantity, int mealType, long date,
                     float totalCalories, float totalProtein, float totalCarbs, float totalFat) {
        this.foodId = foodId;
        this.quantity = quantity;
        this.mealType = mealType;
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
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
    
    public float getQuantity() {
        return quantity;
    }
    
    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
    
    public int getMealType() {
        return mealType;
    }

    public void setMealType(int mealType) {
        this.mealType = mealType;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public float getTotalCalories() {
        return totalCalories;
    }
    
    public void setTotalCalories(float totalCalories) {
        this.totalCalories = totalCalories;
    }
    
    public float getTotalProtein() {
        return totalProtein;
    }
    
    public void setTotalProtein(float totalProtein) {
        this.totalProtein = totalProtein;
    }
    
    public float getTotalCarbs() {
        return totalCarbs;
    }
    
    public void setTotalCarbs(float totalCarbs) {
        this.totalCarbs = totalCarbs;
    }
    
    public float getTotalFat() {
        return totalFat;
    }
    
    public void setTotalFat(float totalFat) {
        this.totalFat = totalFat;
    }
    
    /**
     * Lấy tên hiển thị của loại bữa ăn
     */
    public String getMealTypeDisplayName() {
        switch (mealType) {
            case 0:
                return "Bữa sáng";
            case 1:
                return "Bữa trưa";
            case 2:
                return "Bữa tối";
            case 3:
                return "Bữa phụ";
            default:
                return "Khác";
        }
    }
}

