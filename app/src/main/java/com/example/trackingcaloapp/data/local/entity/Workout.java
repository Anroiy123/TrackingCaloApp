package com.example.trackingcaloapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện cho một loại bài tập trong cơ sở dữ liệu.
 * Lưu trữ thông tin calo đốt cháy per unit (phút, km, lần).
 */
@Entity(tableName = "workouts")
public class Workout {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;              // Tên bài tập (VD: "Chạy bộ")
    private float caloriesPerUnit;    // Calo đốt per unit (VD: 10 cal/phút hoặc 60 cal/km)
    private String unit;              // Đơn vị: "phút", "km", "lần"
    private String category;          // Loại: "cardio", "strength", "flexibility"
    private boolean isCustom;         // true = user tự tạo, false = có sẵn trong app
    
    // Constructor đầy đủ
    public Workout(String name, float caloriesPerUnit, String unit, String category, boolean isCustom) {
        this.name = name;
        this.caloriesPerUnit = caloriesPerUnit;
        this.unit = unit;
        this.category = category;
        this.isCustom = isCustom;
    }
    
    // Constructor cho bài tập có sẵn (isCustom = false)
    @Ignore
    public Workout(String name, float caloriesPerUnit, String unit, String category) {
        this(name, caloriesPerUnit, unit, category, false);
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public float getCaloriesPerUnit() {
        return caloriesPerUnit;
    }
    
    public void setCaloriesPerUnit(float caloriesPerUnit) {
        this.caloriesPerUnit = caloriesPerUnit;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isCustom() {
        return isCustom;
    }
    
    public void setCustom(boolean custom) {
        isCustom = custom;
    }
    
    /**
     * Tính calo đốt cháy dựa trên số lượng thực tế
     * @param quantity Số lượng (phút, km, hoặc lần tùy theo unit)
     * @return Số calo đốt cháy
     */
    public float calculateCaloriesBurned(float quantity) {
        return caloriesPerUnit * quantity;
    }
    
    /**
     * Lấy tên hiển thị của đơn vị
     */
    public String getUnitDisplayName() {
        switch (unit) {
            case "phút":
                return "phút";
            case "km":
                return "km";
            case "lần":
                return "lần";
            default:
                return unit;
        }
    }
    
    /**
     * Lấy tên hiển thị của category
     */
    public String getCategoryDisplayName() {
        switch (category) {
            case "cardio":
                return "Cardio";
            case "strength":
                return "Sức mạnh";
            case "flexibility":
                return "Linh hoạt";
            default:
                return category;
        }
    }
}

