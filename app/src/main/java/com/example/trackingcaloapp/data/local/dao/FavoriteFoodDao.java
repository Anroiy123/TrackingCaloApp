package com.example.trackingcaloapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trackingcaloapp.data.local.entity.FavoriteFood;
import com.example.trackingcaloapp.model.FoodWithDetails;

import java.util.List;

/**
 * Data Access Object cho FavoriteFood entity.
 * Cung cấp các phương thức quản lý thực phẩm yêu thích.
 */
@Dao
public interface FavoriteFoodDao {

    // ==================== INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FavoriteFood favoriteFood);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FavoriteFood> favoriteFoods);

    // ==================== UPDATE ====================

    @Update
    void update(FavoriteFood favoriteFood);

    /**
     * Increment use count và update lastUsed cho một favorite
     */
    @Query("UPDATE favorite_foods SET useCount = useCount + 1, lastUsed = :timestamp WHERE foodId = :foodId")
    void incrementUseCount(int foodId, long timestamp);

    // ==================== DELETE ====================

    @Delete
    void delete(FavoriteFood favoriteFood);

    @Query("DELETE FROM favorite_foods WHERE id = :favoriteId")
    void deleteById(int favoriteId);

    @Query("DELETE FROM favorite_foods WHERE foodId = :foodId")
    void deleteByFoodId(int foodId);

    @Query("DELETE FROM favorite_foods")
    void deleteAll();

    // ==================== QUERY ====================

    /**
     * Lấy tất cả favorites, sort by lastUsed DESC (mới nhất trước)
     */
    @Query("SELECT * FROM favorite_foods ORDER BY lastUsed DESC")
    LiveData<List<FavoriteFood>> getAllFavorites();

    /**
     * Lấy tất cả favorites (không LiveData)
     */
    @Query("SELECT * FROM favorite_foods ORDER BY lastUsed DESC")
    List<FavoriteFood> getAllFavoritesSync();

    /**
     * Lấy top favorites theo useCount (phổ biến nhất)
     */
    @Query("SELECT * FROM favorite_foods ORDER BY useCount DESC LIMIT :limit")
    LiveData<List<FavoriteFood>> getTopFavorites(int limit);

    /**
     * Lấy top favorites (không LiveData)
     */
    @Query("SELECT * FROM favorite_foods ORDER BY useCount DESC LIMIT :limit")
    List<FavoriteFood> getTopFavoritesSync(int limit);

    /**
     * Lấy favorite theo foodId
     */
    @Query("SELECT * FROM favorite_foods WHERE foodId = :foodId LIMIT 1")
    FavoriteFood getFavoriteByFoodId(int foodId);

    /**
     * Lấy favorite theo foodId (LiveData)
     */
    @Query("SELECT * FROM favorite_foods WHERE foodId = :foodId LIMIT 1")
    LiveData<FavoriteFood> getFavoriteByFoodIdLive(int foodId);

    /**
     * Kiểm tra xem food có phải là favorite không
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_foods WHERE foodId = :foodId)")
    boolean isFavorite(int foodId);

    /**
     * Kiểm tra xem food có phải là favorite không (LiveData)
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_foods WHERE foodId = :foodId)")
    LiveData<Boolean> isFavoriteLive(int foodId);

    /**
     * Đếm số lượng favorites
     */
    @Query("SELECT COUNT(*) FROM favorite_foods")
    int getFavoriteCount();

    // ==================== JOIN QUERIES ====================

    /**
     * Lấy danh sách Food kèm thông tin favorite (FoodWithDetails)
     * Sort by lastUsed DESC
     */
    @Query("SELECT f.*, ff.id as favoriteId, ff.defaultQuantity, ff.lastUsed, ff.useCount, " +
           "(ff.id IS NOT NULL) as isFavorite " +
           "FROM foods f " +
           "INNER JOIN favorite_foods ff ON f.id = ff.foodId " +
           "ORDER BY ff.lastUsed DESC")
    LiveData<List<FoodWithDetails>> getAllFavoriteFoodsWithDetails();

    /**
     * Lấy danh sách Food kèm thông tin favorite (không LiveData)
     */
    @Query("SELECT f.*, ff.id as favoriteId, ff.defaultQuantity, ff.lastUsed, ff.useCount, " +
           "(ff.id IS NOT NULL) as isFavorite " +
           "FROM foods f " +
           "INNER JOIN favorite_foods ff ON f.id = ff.foodId " +
           "ORDER BY ff.lastUsed DESC")
    List<FoodWithDetails> getAllFavoriteFoodsWithDetailsSync();

    /**
     * Lấy top favorite foods kèm details
     */
    @Query("SELECT f.*, ff.id as favoriteId, ff.defaultQuantity, ff.lastUsed, ff.useCount, " +
           "(ff.id IS NOT NULL) as isFavorite " +
           "FROM foods f " +
           "INNER JOIN favorite_foods ff ON f.id = ff.foodId " +
           "ORDER BY ff.useCount DESC " +
           "LIMIT :limit")
    LiveData<List<FoodWithDetails>> getTopFavoriteFoodsWithDetails(int limit);
}

