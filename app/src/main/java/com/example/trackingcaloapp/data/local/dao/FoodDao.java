package com.example.trackingcaloapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trackingcaloapp.data.local.entity.Food;

import java.util.List;

/**
 * Data Access Object cho Food entity.
 * Cung cấp các phương thức truy vấn cơ sở dữ liệu thực phẩm.
 */
@Dao
public interface FoodDao {
    
    // ==================== INSERT ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Food food);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Food> foods);
    
    // ==================== UPDATE ====================
    
    @Update
    void update(Food food);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(Food food);
    
    @Query("DELETE FROM foods WHERE id = :foodId")
    void deleteById(int foodId);
    
    @Query("DELETE FROM foods WHERE isCustom = 1")
    void deleteAllCustomFoods();
    
    // ==================== QUERY ====================
    
    /**
     * Lấy tất cả thực phẩm, sắp xếp theo tên
     */
    @Query("SELECT * FROM foods ORDER BY name ASC")
    LiveData<List<Food>> getAllFoods();
    
    /**
     * Lấy tất cả thực phẩm (không LiveData - dùng cho background thread)
     */
    @Query("SELECT * FROM foods ORDER BY name ASC")
    List<Food> getAllFoodsSync();
    
    /**
     * Lấy thực phẩm theo ID
     */
    @Query("SELECT * FROM foods WHERE id = :foodId")
    Food getFoodById(int foodId);
    
    /**
     * Lấy thực phẩm theo ID (LiveData)
     */
    @Query("SELECT * FROM foods WHERE id = :foodId")
    LiveData<Food> getFoodByIdLive(int foodId);
    
    /**
     * Tìm kiếm thực phẩm theo tên
     */
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    LiveData<List<Food>> searchFoods(String searchQuery);
    
    /**
     * Tìm kiếm thực phẩm theo tên (không LiveData)
     */
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    List<Food> searchFoodsSync(String searchQuery);
    
    /**
     * Lấy thực phẩm theo category
     */
    @Query("SELECT * FROM foods WHERE category = :category ORDER BY name ASC")
    LiveData<List<Food>> getFoodsByCategory(String category);

    /**
     * Lấy thực phẩm theo nhiều categories (dùng cho filter theo bữa ăn)
     */
    @Query("SELECT * FROM foods WHERE category IN (:categories) ORDER BY name ASC")
    LiveData<List<Food>> getFoodsByCategories(List<String> categories);

    /**
     * Lấy tất cả thực phẩm do user tự tạo
     */
    @Query("SELECT * FROM foods WHERE isCustom = 1 ORDER BY name ASC")
    LiveData<List<Food>> getCustomFoods();
    
    /**
     * Lấy tất cả thực phẩm có sẵn trong app
     */
    @Query("SELECT * FROM foods WHERE isCustom = 0 ORDER BY name ASC")
    LiveData<List<Food>> getDefaultFoods();
    
    /**
     * Đếm số lượng thực phẩm trong database
     */
    @Query("SELECT COUNT(*) FROM foods")
    int getFoodCount();
    
    /**
     * Lấy danh sách các category có trong database
     */
    @Query("SELECT DISTINCT category FROM foods ORDER BY category ASC")
    LiveData<List<String>> getAllCategories();

    // ==================== API INTEGRATION ====================

    /**
     * Tìm food theo API ID (để check cache)
     */
    @Query("SELECT * FROM foods WHERE apiId = :apiId AND apiSource = 'fatsecret' LIMIT 1")
    Food getFoodByApiId(long apiId);

    /**
     * Lấy tất cả foods từ API (cached)
     */
    @Query("SELECT * FROM foods WHERE apiSource = 'fatsecret' ORDER BY name ASC")
    LiveData<List<Food>> getApiFoods();

    /**
     * Xóa cached foods cũ hơn X timestamp
     */
    @Query("DELETE FROM foods WHERE apiSource = 'fatsecret' AND cachedAt < :timestamp")
    void deleteOldCachedFoods(long timestamp);

    /**
     * Search tất cả foods (local + API), local foods hiện trước
     */
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' " +
           "ORDER BY CASE WHEN apiSource IS NULL THEN 0 ELSE 1 END, name ASC")
    LiveData<List<Food>> searchAllFoods(String query);

    /**
     * Đếm số lượng cached foods từ API
     */
    @Query("SELECT COUNT(*) FROM foods WHERE apiSource = 'fatsecret'")
    int getApiCachedFoodCount();
}

