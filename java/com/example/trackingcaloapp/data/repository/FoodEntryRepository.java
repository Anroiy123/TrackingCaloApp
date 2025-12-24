package com.example.trackingcaloapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackingcaloapp.data.local.dao.FoodEntryDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.model.DailySummary;
import com.example.trackingcaloapp.model.FoodEntryWithFood;
import com.example.trackingcaloapp.utils.DateUtils;

import java.util.List;

/**
 * Repository cho FoodEntry entity.
 * Cung cấp abstraction layer giữa ViewModel và data sources.
 */
public class FoodEntryRepository {

    private final FoodEntryDao foodEntryDao;

    public FoodEntryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        foodEntryDao = db.foodEntryDao();
    }

    // Constructor overload for direct DAO injection
    public FoodEntryRepository(FoodEntryDao foodEntryDao) {
        this.foodEntryDao = foodEntryDao;
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Lấy tất cả entries
     */
    public LiveData<List<FoodEntry>> getAllEntries() {
        return foodEntryDao.getAllEntries();
    }
    
    /**
     * Lấy entry theo ID
     */
    public FoodEntry getEntryById(int entryId) {
        return foodEntryDao.getEntryById(entryId);
    }
    
    /**
     * Lấy entries trong một ngày
     */
    public LiveData<List<FoodEntry>> getEntriesByDate(long startOfDay, long endOfDay) {
        return foodEntryDao.getEntriesByDate(startOfDay, endOfDay);
    }
    
    /**
     * Lấy entries theo loại bữa ăn trong một ngày
     */
    public LiveData<List<FoodEntry>> getEntriesByDateAndMealType(long startOfDay, long endOfDay, String mealType) {
        return foodEntryDao.getEntriesByDateAndMealType(startOfDay, endOfDay, mealType);
    }
    
    // ==================== AGGREGATION ====================
    
    /**
     * Tính tổng calo trong một ngày
     */
    public LiveData<Float> getTotalCaloriesByDate(long startOfDay, long endOfDay) {
        return foodEntryDao.getTotalCaloriesByDate(startOfDay, endOfDay);
    }
    
    /**
     * Tính tổng protein trong một ngày
     */
    public LiveData<Float> getTotalProteinByDate(long startOfDay, long endOfDay) {
        return foodEntryDao.getTotalProteinByDate(startOfDay, endOfDay);
    }
    
    /**
     * Tính tổng carbs trong một ngày
     */
    public LiveData<Float> getTotalCarbsByDate(long startOfDay, long endOfDay) {
        return foodEntryDao.getTotalCarbsByDate(startOfDay, endOfDay);
    }
    
    /**
     * Tính tổng fat trong một ngày
     */
    public LiveData<Float> getTotalFatByDate(long startOfDay, long endOfDay) {
        return foodEntryDao.getTotalFatByDate(startOfDay, endOfDay);
    }
    
    /**
     * Tính tổng calo theo loại bữa ăn
     */
    public LiveData<Float> getTotalCaloriesByMealType(long startOfDay, long endOfDay, String mealType) {
        return foodEntryDao.getTotalCaloriesByMealType(startOfDay, endOfDay, mealType);
    }
    
    // ==================== INSERT ====================
    
    /**
     * Thêm food entry mới
     */
    public void insert(FoodEntry foodEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodEntryDao.insert(foodEntry);
        });
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Cập nhật food entry
     */
    public void update(FoodEntry foodEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodEntryDao.update(foodEntry);
        });
    }
    
    // ==================== DELETE ====================
    
    /**
     * Xóa food entry
     */
    public void delete(FoodEntry foodEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodEntryDao.delete(foodEntry);
        });
    }
    
    /**
     * Xóa food entry theo ID
     */
    public void deleteById(int entryId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodEntryDao.deleteById(entryId);
        });
    }

    // ==================== ENTRIES WITH FOOD ====================

    /**
     * Lấy entries trong một ngày kèm thông tin Food
     */
    public LiveData<List<FoodEntryWithFood>> getEntriesWithFoodByDate(long startOfDay, long endOfDay) {
        return foodEntryDao.getEntriesWithFoodByDate(startOfDay, endOfDay);
    }

    /**
     * Lấy entries trong một ngày kèm thông tin Food (không LiveData)
     */
    public List<FoodEntryWithFood> getEntriesWithFoodByDateSync(long startOfDay, long endOfDay) {
        return foodEntryDao.getEntriesWithFoodByDateSync(startOfDay, endOfDay);
    }

    /**
     * Lấy entries theo meal type trong một ngày kèm thông tin Food
     */
    public LiveData<List<FoodEntryWithFood>> getEntriesWithFoodByMealType(long startOfDay, long endOfDay, int mealType) {
        return foodEntryDao.getEntriesWithFoodByMealType(startOfDay, endOfDay, mealType);
    }

    /**
     * Lấy entry theo ID kèm thông tin Food
     */
    public FoodEntryWithFood getEntryWithFoodById(int entryId) {
        return foodEntryDao.getEntryWithFoodById(entryId);
    }

    // ==================== DAILY SUMMARY ====================

    /**
     * Tính toán DailySummary cho một ngày
     * @param date Timestamp của ngày (bất kỳ thời điểm trong ngày)
     * @param dailyGoal Mục tiêu calo/ngày
     * @param caloriesBurned Calo đốt cháy từ workout
     * @return DailySummary đã tính toán đầy đủ
     */
    public DailySummary getDailySummary(long date, int dailyGoal, float caloriesBurned) {
        long startOfDay = DateUtils.getStartOfDay(date);
        long endOfDay = DateUtils.getEndOfDay(date);

        DailySummary summary = new DailySummary(date, dailyGoal);

        // Lấy totals từ database
        float totalCalories = foodEntryDao.getTotalCaloriesByDateSync(startOfDay, endOfDay);

        summary.setCaloriesConsumed(totalCalories);
        summary.setCaloriesBurned(caloriesBurned);

        // Lấy entries sync
        List<FoodEntry> entries = foodEntryDao.getEntriesByDateSync(startOfDay, endOfDay);
        if (entries != null) {
            float proteinTotal = 0;
            float carbsTotal = 0;
            float fatTotal = 0;
            for (FoodEntry entry : entries) {
                proteinTotal += entry.getTotalProtein();
                carbsTotal += entry.getTotalCarbs();
                fatTotal += entry.getTotalFat();
            }
            summary.setProteinTotal(proteinTotal);
            summary.setCarbsTotal(carbsTotal);
            summary.setFatTotal(fatTotal);
            summary.setFoodEntries(entries);
        }

        summary.calculateMacroGoals();
        return summary;
    }
}

