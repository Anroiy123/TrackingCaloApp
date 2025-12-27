package com.example.trackingcaloapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackingcaloapp.data.local.dao.FoodEntryDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.model.DailyCalorieSum;
import com.example.trackingcaloapp.model.HourlyCalorieSum;
import com.example.trackingcaloapp.model.MacroSum;
import com.example.trackingcaloapp.model.MealTypeCalories;

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

    // ==================== CHART AGGREGATION ====================

    /**
     * Lấy tổng calo theo từng ngày trong khoảng thời gian (cho LineChart)
     */
    public LiveData<List<DailyCalorieSum>> getDailyCaloriesSummary(long startDate, long endDate) {
        return foodEntryDao.getDailyCaloriesSummary(startDate, endDate);
    }

    /**
     * Lấy tổng calo theo loại bữa ăn trong khoảng thời gian (cho BarChart)
     */
    public LiveData<List<MealTypeCalories>> getCaloriesByMealType(long startDate, long endDate) {
        return foodEntryDao.getCaloriesByMealType(startDate, endDate);
    }

    /**
     * Lấy tổng macro nutrients trong khoảng thời gian (cho PieChart)
     */
    public LiveData<MacroSum> getMacroSummary(long startDate, long endDate) {
        return foodEntryDao.getMacroSummary(startDate, endDate);
    }

    /**
     * Lấy tổng calo theo giờ trong ngày (cho LineChart trong DiaryFragment)
     */
    public LiveData<List<HourlyCalorieSum>> getHourlyCaloriesSummary(long startDate, long endDate) {
        return foodEntryDao.getHourlyCaloriesSummary(startDate, endDate);
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
}

