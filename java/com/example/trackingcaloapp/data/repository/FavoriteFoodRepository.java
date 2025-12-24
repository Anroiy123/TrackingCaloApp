package com.example.trackingcaloapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackingcaloapp.data.local.dao.FavoriteFoodDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.FavoriteFood;
import com.example.trackingcaloapp.model.FoodWithDetails;

import java.util.List;

/**
 * Repository cho FavoriteFood entity.
 * Cung cấp abstraction layer giữa ViewModel và data sources.
 */
public class FavoriteFoodRepository {

    private final FavoriteFoodDao favoriteFoodDao;

    public FavoriteFoodRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        favoriteFoodDao = db.favoriteFoodDao();
    }

    // Constructor overload for direct DAO injection
    public FavoriteFoodRepository(FavoriteFoodDao favoriteFoodDao) {
        this.favoriteFoodDao = favoriteFoodDao;
    }

    // ==================== GETTERS ====================

    /**
     * Lấy tất cả favorites (sort by lastUsed DESC)
     */
    public LiveData<List<FavoriteFood>> getAllFavorites() {
        return favoriteFoodDao.getAllFavorites();
    }

    /**
     * Lấy tất cả favorites (không LiveData)
     */
    public List<FavoriteFood> getAllFavoritesSync() {
        return favoriteFoodDao.getAllFavoritesSync();
    }

    /**
     * Lấy top favorites theo useCount
     */
    public LiveData<List<FavoriteFood>> getTopFavorites(int limit) {
        return favoriteFoodDao.getTopFavorites(limit);
    }

    /**
     * Lấy favorite theo foodId
     */
    public FavoriteFood getFavoriteByFoodId(int foodId) {
        return favoriteFoodDao.getFavoriteByFoodId(foodId);
    }

    /**
     * Kiểm tra food có phải là favorite không
     */
    public boolean isFavorite(int foodId) {
        return favoriteFoodDao.isFavorite(foodId);
    }

    /**
     * Kiểm tra food có phải là favorite không (LiveData)
     */
    public LiveData<Boolean> isFavoriteLive(int foodId) {
        return favoriteFoodDao.isFavoriteLive(foodId);
    }

    /**
     * Lấy danh sách Food kèm thông tin favorite
     */
    public LiveData<List<FoodWithDetails>> getAllFavoriteFoodsWithDetails() {
        return favoriteFoodDao.getAllFavoriteFoodsWithDetails();
    }

    /**
     * Lấy top favorite foods kèm details
     */
    public LiveData<List<FoodWithDetails>> getTopFavoriteFoodsWithDetails(int limit) {
        return favoriteFoodDao.getTopFavoriteFoodsWithDetails(limit);
    }

    // ==================== INSERT ====================

    /**
     * Thêm food vào favorites
     */
    public void insert(FavoriteFood favoriteFood) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteFoodDao.insert(favoriteFood);
        });
    }

    /**
     * Thêm food vào favorites với default quantity
     */
    public void addToFavorites(int foodId, float defaultQuantity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            FavoriteFood favorite = new FavoriteFood(foodId, defaultQuantity);
            favoriteFoodDao.insert(favorite);
        });
    }

    // ==================== UPDATE ====================

    /**
     * Cập nhật favorite
     */
    public void update(FavoriteFood favoriteFood) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteFoodDao.update(favoriteFood);
        });
    }

    /**
     * Increment use count khi dùng favorite
     */
    public void incrementUseCount(int foodId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteFoodDao.incrementUseCount(foodId, System.currentTimeMillis());
        });
    }

    // ==================== DELETE ====================

    /**
     * Xóa favorite
     */
    public void delete(FavoriteFood favoriteFood) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteFoodDao.delete(favoriteFood);
        });
    }

    /**
     * Xóa favorite theo foodId
     */
    public void removeFromFavorites(int foodId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteFoodDao.deleteByFoodId(foodId);
        });
    }

    /**
     * Xóa tất cả favorites
     */
    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteFoodDao.deleteAll();
        });
    }

    // ==================== TOGGLE ====================

    /**
     * Toggle favorite status của một food
     * @param foodId ID của food
     * @param defaultQuantity Số lượng mặc định nếu thêm mới
     * @param callback Callback để nhận kết quả (true = đã thêm, false = đã xóa)
     */
    public void toggleFavorite(int foodId, float defaultQuantity, OnToggleCallback callback) {
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
     * Interface callback cho toggle operation
     */
    public interface OnToggleCallback {
        void onResult(boolean isNowFavorite);
    }
}

