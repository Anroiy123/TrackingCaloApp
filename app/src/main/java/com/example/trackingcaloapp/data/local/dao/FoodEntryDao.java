package com.example.trackingcaloapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.model.DailyCalorieSum;
import com.example.trackingcaloapp.model.HourlyCalorieSum;
import com.example.trackingcaloapp.model.MacroSum;
import com.example.trackingcaloapp.model.MealTypeCalories;

import java.util.List;

/**
 * Data Access Object cho FoodEntry entity.
 * Cung cấp các phương thức truy vấn nhật ký ăn uống.
 */
@Dao
public interface FoodEntryDao {
    
    // ==================== INSERT ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FoodEntry foodEntry);
    
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
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY mealType ASC, date ASC")
    LiveData<List<FoodEntry>> getEntriesByDate(long startOfDay, long endOfDay);
    
    /**
     * Lấy các entries trong một ngày (không LiveData)
     */
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY mealType ASC, date ASC")
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

    // ==================== CHART AGGREGATION ====================

    /**
     * Lấy tổng calo theo từng ngày trong khoảng thời gian (cho LineChart)
     * Group by ngày (chia timestamp cho 86400000 ms = 1 ngày)
     */
    @Query("SELECT (date / 86400000) * 86400000 as dayTimestamp, " +
           "COALESCE(SUM(totalCalories), 0) as totalCalories " +
           "FROM food_entries WHERE date BETWEEN :startDate AND :endDate " +
           "GROUP BY date / 86400000 ORDER BY dayTimestamp ASC")
    LiveData<List<DailyCalorieSum>> getDailyCaloriesSummary(long startDate, long endDate);

    /**
     * Lấy tổng calo theo loại bữa ăn trong khoảng thời gian (cho BarChart)
     */
    @Query("SELECT mealType, COALESCE(SUM(totalCalories), 0) as totalCalories " +
           "FROM food_entries WHERE date BETWEEN :startDate AND :endDate " +
           "GROUP BY mealType ORDER BY mealType ASC")
    LiveData<List<MealTypeCalories>> getCaloriesByMealType(long startDate, long endDate);

    /**
     * Lấy tổng macro nutrients trong khoảng thời gian (cho PieChart)
     */
    @Query("SELECT COALESCE(SUM(totalProtein), 0) as protein, " +
           "COALESCE(SUM(totalCarbs), 0) as carbs, " +
           "COALESCE(SUM(totalFat), 0) as fat " +
           "FROM food_entries WHERE date BETWEEN :startDate AND :endDate")
    LiveData<MacroSum> getMacroSummary(long startDate, long endDate);

    /**
     * Lấy tổng calo theo giờ trong ngày (cho LineChart trong DiaryFragment)
     * Extract hour từ timestamp: (date % 86400000) / 3600000
     * 86400000 ms = 1 ngày, 3600000 ms = 1 giờ
     */
    @Query("SELECT ((date % 86400000) / 3600000) as hour, " +
           "COALESCE(SUM(totalCalories), 0) as totalCalories " +
           "FROM food_entries WHERE date BETWEEN :startDate AND :endDate " +
           "GROUP BY (date % 86400000) / 3600000 ORDER BY hour ASC")
    LiveData<List<HourlyCalorieSum>> getHourlyCaloriesSummary(long startDate, long endDate);
}

