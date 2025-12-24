package com.example.trackingcaloapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackingcaloapp.data.local.dao.WorkoutEntryDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;

import java.util.List;

/**
 * Repository cho WorkoutEntry entity.
 * Cung cấp abstraction layer giữa ViewModel và data sources.
 */
public class WorkoutEntryRepository {

    private final WorkoutEntryDao workoutEntryDao;

    public WorkoutEntryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        workoutEntryDao = db.workoutEntryDao();
    }

    // Constructor overload for direct DAO injection
    public WorkoutEntryRepository(WorkoutEntryDao workoutEntryDao) {
        this.workoutEntryDao = workoutEntryDao;
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Lấy tất cả entries
     */
    public LiveData<List<WorkoutEntry>> getAllEntries() {
        return workoutEntryDao.getAllEntries();
    }
    
    /**
     * Lấy entry theo ID
     */
    public WorkoutEntry getEntryById(int entryId) {
        return workoutEntryDao.getEntryById(entryId);
    }
    
    /**
     * Lấy entries trong một ngày
     */
    public LiveData<List<WorkoutEntry>> getEntriesByDate(long startOfDay, long endOfDay) {
        return workoutEntryDao.getEntriesByDate(startOfDay, endOfDay);
    }
    
    // ==================== AGGREGATION ====================
    
    /**
     * Tính tổng calo đốt cháy trong một ngày
     */
    public LiveData<Float> getTotalCaloriesBurnedByDate(long startOfDay, long endOfDay) {
        return workoutEntryDao.getTotalCaloriesBurnedByDate(startOfDay, endOfDay);
    }
    
    /**
     * Tính tổng thời gian tập trong một ngày
     */
    public LiveData<Integer> getTotalDurationByDate(long startOfDay, long endOfDay) {
        return workoutEntryDao.getTotalDurationByDate(startOfDay, endOfDay);
    }
    
    // ==================== INSERT ====================
    
    /**
     * Thêm workout entry mới
     */
    public void insert(WorkoutEntry workoutEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            workoutEntryDao.insert(workoutEntry);
        });
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Cập nhật workout entry
     */
    public void update(WorkoutEntry workoutEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            workoutEntryDao.update(workoutEntry);
        });
    }
    
    // ==================== DELETE ====================
    
    /**
     * Xóa workout entry
     */
    public void delete(WorkoutEntry workoutEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            workoutEntryDao.delete(workoutEntry);
        });
    }
    
    /**
     * Xóa workout entry theo ID
     */
    public void deleteById(int entryId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            workoutEntryDao.deleteById(entryId);
        });
    }
}

