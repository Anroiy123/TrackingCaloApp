package com.example.trackingcaloapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackingcaloapp.data.local.dao.FoodDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.Food;

import java.util.List;

/**
 * Repository cho Food entity.
 * Cung cấp abstraction layer giữa ViewModel và data sources.
 */
public class FoodRepository {

    private final FoodDao foodDao;
    private final LiveData<List<Food>> allFoods;

    public FoodRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        foodDao = db.foodDao();
        allFoods = foodDao.getAllFoods();
    }

    // Constructor overload for direct DAO injection
    public FoodRepository(FoodDao foodDao) {
        this.foodDao = foodDao;
        this.allFoods = foodDao.getAllFoods();
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Lấy tất cả thực phẩm
     */
    public LiveData<List<Food>> getAllFoods() {
        return allFoods;
    }
    
    /**
     * Lấy thực phẩm theo ID
     */
    public Food getFoodById(int foodId) {
        return foodDao.getFoodById(foodId);
    }
    
    /**
     * Lấy thực phẩm theo ID (LiveData)
     */
    public LiveData<Food> getFoodByIdLive(int foodId) {
        return foodDao.getFoodByIdLive(foodId);
    }
    
    /**
     * Tìm kiếm thực phẩm theo tên
     */
    public LiveData<List<Food>> searchFoods(String query) {
        return foodDao.searchFoods(query);
    }
    
    /**
     * Lấy thực phẩm theo category
     */
    public LiveData<List<Food>> getFoodsByCategory(String category) {
        return foodDao.getFoodsByCategory(category);
    }
    
    /**
     * Lấy thực phẩm do user tự tạo
     */
    public LiveData<List<Food>> getCustomFoods() {
        return foodDao.getCustomFoods();
    }
    
    /**
     * Lấy danh sách categories
     */
    public LiveData<List<String>> getAllCategories() {
        return foodDao.getAllCategories();
    }
    
    // ==================== INSERT ====================
    
    /**
     * Thêm thực phẩm mới
     */
    public void insert(Food food) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.insert(food);
        });
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Cập nhật thực phẩm
     */
    public void update(Food food) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.update(food);
        });
    }
    
    // ==================== DELETE ====================
    
    /**
     * Xóa thực phẩm
     */
    public void delete(Food food) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.delete(food);
        });
    }
    
    /**
     * Xóa thực phẩm theo ID
     */
    public void deleteById(int foodId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.deleteById(foodId);
        });
    }
}

