package com.example.trackingcaloapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackingcaloapp.data.local.dao.FavoriteFoodDao;
import com.example.trackingcaloapp.data.local.dao.FoodDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.FavoriteFood;
import com.example.trackingcaloapp.data.local.entity.Food;
import com.example.trackingcaloapp.model.FoodWithDetails;

import java.util.List;

/**
 * Repository cho Food entity.
 * Cung cấp abstraction layer giữa ViewModel và data sources.
 */
public class FoodRepository {

    private final FoodDao foodDao;
    private final FavoriteFoodDao favoriteFoodDao;
    private final LiveData<List<Food>> allFoods;

    public FoodRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        foodDao = db.foodDao();
        favoriteFoodDao = db.favoriteFoodDao();
        allFoods = foodDao.getAllFoods();
    }

    // Constructor overload for direct DAO injection
    public FoodRepository(FoodDao foodDao) {
        this.foodDao = foodDao;
        this.favoriteFoodDao = null;
        this.allFoods = foodDao.getAllFoods();
    }

    // Constructor overload for both DAOs
    public FoodRepository(FoodDao foodDao, FavoriteFoodDao favoriteFoodDao) {
        this.foodDao = foodDao;
        this.favoriteFoodDao = favoriteFoodDao;
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

    // ==================== FOOD WITH DETAILS ====================

    /**
     * Lấy tất cả foods kèm thông tin favorite
     */
    public LiveData<List<FoodWithDetails>> getAllFoodsWithDetails() {
        return foodDao.getAllFoodsWithDetails();
    }

    /**
     * Tìm kiếm foods kèm thông tin favorite (search name + aliasVi)
     */
    public LiveData<List<FoodWithDetails>> searchFoodsWithDetails(String query) {
        return foodDao.searchFoodsWithDetails(query);
    }

    /**
     * Lấy foods theo category kèm thông tin favorite
     */
    public LiveData<List<FoodWithDetails>> getFoodsByCategoryWithDetails(String category) {
        return foodDao.getFoodsByCategoryWithDetails(category);
    }

    /**
     * Lấy một food theo ID kèm thông tin favorite
     */
    public FoodWithDetails getFoodWithDetailsById(int foodId) {
        return foodDao.getFoodWithDetailsById(foodId);
    }

    // ==================== FAVORITES ====================

    /**
     * Lấy tất cả favorites
     */
    public LiveData<List<FavoriteFood>> getAllFavorites() {
        if (favoriteFoodDao == null) return null;
        return favoriteFoodDao.getAllFavorites();
    }

    /**
     * Lấy danh sách Food yêu thích kèm thông tin chi tiết
     */
    public LiveData<List<FoodWithDetails>> getAllFavoriteFoodsWithDetails() {
        if (favoriteFoodDao == null) return null;
        return favoriteFoodDao.getAllFavoriteFoodsWithDetails();
    }

    /**
     * Toggle favorite status của một food
     */
    public void toggleFavorite(int foodId, float defaultQuantity, OnToggleCallback callback) {
        if (favoriteFoodDao == null) {
            if (callback != null) callback.onResult(false);
            return;
        }
        AppDatabase.databaseWriteExecutor.execute(() -> {
            boolean isFav = favoriteFoodDao.isFavorite(foodId);
            if (isFav) {
                favoriteFoodDao.deleteByFoodId(foodId);
                if (callback != null) callback.onResult(false);
            } else {
                FavoriteFood favorite = new FavoriteFood(foodId, defaultQuantity);
                favoriteFoodDao.insert(favorite);
                if (callback != null) callback.onResult(true);
            }
        });
    }

    /**
     * Increment use count khi dùng favorite
     */
    public void incrementFavoriteUseCount(int foodId) {
        if (favoriteFoodDao == null) return;
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteFoodDao.incrementUseCount(foodId, System.currentTimeMillis());
        });
    }

    /**
     * Kiểm tra food có phải là favorite không
     */
    public boolean isFavorite(int foodId) {
        if (favoriteFoodDao == null) return false;
        return favoriteFoodDao.isFavorite(foodId);
    }

    /**
     * Interface callback cho toggle operation
     */
    public interface OnToggleCallback {
        void onResult(boolean isNowFavorite);
    }
}

