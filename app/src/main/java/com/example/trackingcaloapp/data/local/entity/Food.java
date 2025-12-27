package com.example.trackingcaloapp.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện cho một loại thực phẩm trong cơ sở dữ liệu.
 * Lưu trữ thông tin dinh dưỡng per 100g.
 */
@Entity(tableName = "foods")
public class Food {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;           // Tên thực phẩm (VD: "Phở bò")
    private float calories;        // Calo per 100g
    private float protein;         // Protein (g) per 100g
    private float carbs;           // Carbohydrate (g) per 100g
    private float fat;             // Chất béo (g) per 100g
    private String category;       // Loại: "com", "pho", "thit", "rau", "trai_cay", "do_uong", "an_vat", "api"
    private boolean isCustom;      // true = user tự tạo, false = có sẵn trong app

    // API Integration fields
    @ColumnInfo(name = "apiId")
    private Long apiId;            // FatSecret food_id (null = local food)

    @ColumnInfo(name = "apiSource")
    private String apiSource;      // "fatsecret" hoặc null

    @ColumnInfo(name = "cachedAt", defaultValue = "0")
    private long cachedAt;         // Timestamp khi cache từ API
    
    // Constructor đầy đủ
    public Food(String name, float calories, float protein, float carbs, float fat, String category, boolean isCustom) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.category = category;
        this.isCustom = isCustom;
    }
    
    // Constructor cho thực phẩm có sẵn (isCustom = false)
    @Ignore
    public Food(String name, float calories, float protein, float carbs, float fat, String category) {
        this(name, calories, protein, carbs, fat, category, false);
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
    
    public float getCalories() {
        return calories;
    }
    
    public void setCalories(float calories) {
        this.calories = calories;
    }
    
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

    // API fields getters/setters
    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getApiSource() {
        return apiSource;
    }

    public void setApiSource(String apiSource) {
        this.apiSource = apiSource;
    }

    public long getCachedAt() {
        return cachedAt;
    }

    public void setCachedAt(long cachedAt) {
        this.cachedAt = cachedAt;
    }

    /**
     * Check if this food is from external API
     */
    public boolean isFromApi() {
        return apiSource != null && !apiSource.isEmpty();
    }

    /**
     * Tính calo dựa trên khối lượng thực tế (gram)
     * @param grams Khối lượng thực phẩm (gram)
     * @return Số calo tương ứng
     */
    public float calculateCalories(float grams) {
        return (calories * grams) / 100f;
    }
    
    /**
     * Tính protein dựa trên khối lượng thực tế (gram)
     */
    public float calculateProtein(float grams) {
        return (protein * grams) / 100f;
    }
    
    /**
     * Tính carbs dựa trên khối lượng thực tế (gram)
     */
    public float calculateCarbs(float grams) {
        return (carbs * grams) / 100f;
    }
    
    /**
     * Tính fat dựa trên khối lượng thực tế (gram)
     */
    public float calculateFat(float grams) {
        return (fat * grams) / 100f;
    }
}

