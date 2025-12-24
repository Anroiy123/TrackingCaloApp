package com.example.trackingcaloapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.model.FoodEntryWithFood;

import java.util.List;

/**
 * Data Access Object cho FoodEntry entity.
 * Cung cấp các phương thức truy vấn nhật ký ăn uống.
 */
@Dao
public interface FoodEntryDao {
    
    // ==================== INSERT ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FoodEntry foodEntry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FoodEntry> foodEntries);
    
    // ==================== UPDATE ====================
    
    @Update
    void update(FoodEntry foodEntry);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(FoodEntry foodEntry);
    
    @Query("DELETE FROM food_entries WHERE id = :entryId")
    void deleteById(int entryId);
    
    @Query("DELETE FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    void deleteEntriesByDate(long startOfDay, long endOfDay);
    
    // ==================== QUERY ====================
    
    /**
     * Lấy tất cả food entries
     */
    @Query("SELECT * FROM food_entries ORDER BY date DESC")
    LiveData<List<FoodEntry>> getAllEntries();
    
    /**
     * Lấy food entry theo ID
     */
    @Query("SELECT * FROM food_entries WHERE id = :entryId")
    FoodEntry getEntryById(int entryId);
    
    /**
     * Lấy các entries trong một ngày cụ thể
     * @param startOfDay Timestamp đầu ngày (00:00:00)
     * @param endOfDay Timestamp cuối ngày (23:59:59)
     */
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date ASC")
    LiveData<List<FoodEntry>> getEntriesByDate(long startOfDay, long endOfDay);
    
    /**
     * Lấy các entries trong một ngày (không LiveData)
     */
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date ASC")
    List<FoodEntry> getEntriesByDateSync(long startOfDay, long endOfDay);
    
    /**
     * Lấy các entries theo loại bữa ăn trong một ngày
     */
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay AND mealType = :mealType ORDER BY date ASC")
    LiveData<List<FoodEntry>> getEntriesByDateAndMealType(long startOfDay, long endOfDay, String mealType);
    
    // ==================== AGGREGATION ====================
    
    /**
     * Tính tổng calo trong một ngày
     */
    @Query("SELECT COALESCE(SUM(totalCalories), 0) FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<Float> getTotalCaloriesByDate(long startOfDay, long endOfDay);
    
    /**
     * Tính tổng calo trong một ngày (không LiveData)
     */
    @Query("SELECT COALESCE(SUM(totalCalories), 0) FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    float getTotalCaloriesByDateSync(long startOfDay, long endOfDay);
    
    /**
     * Tính tổng protein trong một ngày
     */
    @Query("SELECT COALESCE(SUM(totalProtein), 0) FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<Float> getTotalProteinByDate(long startOfDay, long endOfDay);
    
    /**
     * Tính tổng carbs trong một ngày
     */
    @Query("SELECT COALESCE(SUM(totalCarbs), 0) FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<Float> getTotalCarbsByDate(long startOfDay, long endOfDay);
    
    /**
     * Tính tổng fat trong một ngày
     */
    @Query("SELECT COALESCE(SUM(totalFat), 0) FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<Float> getTotalFatByDate(long startOfDay, long endOfDay);
    
    /**
     * Tính tổng calo theo loại bữa ăn trong một ngày
     */
    @Query("SELECT COALESCE(SUM(totalCalories), 0) FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay AND mealType = :mealType")
    LiveData<Float> getTotalCaloriesByMealType(long startOfDay, long endOfDay, String mealType);
    
    /**
     * Đếm số entries trong một ngày
     */
    @Query("SELECT COUNT(*) FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    int getEntryCountByDate(long startOfDay, long endOfDay);

    // ==================== ENTRIES WITH FOOD (JOIN) ====================

    /**
     * Lấy entries trong một ngày kèm thông tin Food
     */
    @Transaction
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY mealType ASC, date ASC")
    LiveData<List<FoodEntryWithFood>> getEntriesWithFoodByDate(long startOfDay, long endOfDay);

    /**
     * Lấy entries trong một ngày kèm thông tin Food (không LiveData)
     */
    @Transaction
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY mealType ASC, date ASC")
    List<FoodEntryWithFood> getEntriesWithFoodByDateSync(long startOfDay, long endOfDay);

    /**
     * Lấy entries theo meal type trong một ngày kèm thông tin Food
     */
    @Transaction
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay AND mealType = :mealType ORDER BY date ASC")
    LiveData<List<FoodEntryWithFood>> getEntriesWithFoodByMealType(long startOfDay, long endOfDay, int mealType);

    /**
     * Lấy tất cả entries kèm thông tin Food (không LiveData)
     */
    @Transaction
    @Query("SELECT * FROM food_entries ORDER BY date DESC")
    List<FoodEntryWithFood> getAllEntriesWithFoodSync();

    /**
     * Lấy entry theo ID kèm thông tin Food
     */
    @Transaction
    @Query("SELECT * FROM food_entries WHERE id = :entryId")
    FoodEntryWithFood getEntryWithFoodById(int entryId);
}

