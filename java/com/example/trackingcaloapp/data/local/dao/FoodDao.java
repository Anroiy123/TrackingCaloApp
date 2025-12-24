package com.example.trackingcaloapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.trackingcaloapp.data.local.entity.Food;
import com.example.trackingcaloapp.model.FoodWithDetails;

import java.util.List;

/**
 * Data Access Object cho Food entity.
 * Cung cấp các phương thức truy vấn cơ sở dữ liệu thực phẩm.
 */
@Dao
public interface FoodDao {
    
    // ==================== INSERT ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Food food);

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
     * Tìm kiếm thực phẩm theo tên hoặc alias tiếng Việt
     */
    @Query("SELECT * FROM foods WHERE LOWER(name) LIKE '%' || LOWER(:searchQuery) || '%' " +
           "OR LOWER(aliasVi) LIKE '%' || LOWER(:searchQuery) || '%' ORDER BY name ASC")
    LiveData<List<Food>> searchFoods(String searchQuery);
    
    /**
     * Tìm kiếm thực phẩm theo tên hoặc alias (không LiveData)
     */
    @Query("SELECT * FROM foods WHERE LOWER(name) LIKE '%' || LOWER(:searchQuery) || '%' " +
           "OR LOWER(aliasVi) LIKE '%' || LOWER(:searchQuery) || '%' ORDER BY name ASC")
    List<Food> searchFoodsSync(String searchQuery);
    
    /**
     * Lấy thực phẩm theo category
     */
    @Query("SELECT * FROM foods WHERE category = :category ORDER BY name ASC")
    LiveData<List<Food>> getFoodsByCategory(String category);
    
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

    // ==================== FOOD WITH DETAILS (JOIN với FavoriteFood) ====================

    /**
     * Lấy tất cả foods kèm thông tin favorite
     */
    @Query("SELECT f.*, ff.id as favoriteId, ff.defaultQuantity, ff.lastUsed, ff.useCount, " +
           "(ff.id IS NOT NULL) as isFavorite " +
           "FROM foods f " +
           "LEFT JOIN favorite_foods ff ON f.id = ff.foodId " +
           "ORDER BY f.name ASC")
    LiveData<List<FoodWithDetails>> getAllFoodsWithDetails();

    /**
     * Tìm kiếm foods kèm thông tin favorite (search name + aliasVi)
     */
    @Query("SELECT f.*, ff.id as favoriteId, ff.defaultQuantity, ff.lastUsed, ff.useCount, " +
           "(ff.id IS NOT NULL) as isFavorite " +
           "FROM foods f " +
           "LEFT JOIN favorite_foods ff ON f.id = ff.foodId " +
           "WHERE LOWER(f.name) LIKE '%' || LOWER(:searchQuery) || '%' " +
           "OR LOWER(f.aliasVi) LIKE '%' || LOWER(:searchQuery) || '%' " +
           "ORDER BY f.name ASC")
    LiveData<List<FoodWithDetails>> searchFoodsWithDetails(String searchQuery);

    /**
     * Lấy foods theo category kèm thông tin favorite
     */
    @Query("SELECT f.*, ff.id as favoriteId, ff.defaultQuantity, ff.lastUsed, ff.useCount, " +
           "(ff.id IS NOT NULL) as isFavorite " +
           "FROM foods f " +
           "LEFT JOIN favorite_foods ff ON f.id = ff.foodId " +
           "WHERE f.category = :category " +
           "ORDER BY f.name ASC")
    LiveData<List<FoodWithDetails>> getFoodsByCategoryWithDetails(String category);

    /**
     * Lấy một food theo ID kèm thông tin favorite
     */
    @Query("SELECT f.*, ff.id as favoriteId, ff.defaultQuantity, ff.lastUsed, ff.useCount, " +
           "(ff.id IS NOT NULL) as isFavorite " +
           "FROM foods f " +
           "LEFT JOIN favorite_foods ff ON f.id = ff.foodId " +
           "WHERE f.id = :foodId")
    FoodWithDetails getFoodWithDetailsById(int foodId);
}

