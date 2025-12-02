package com.example.trackingcaloapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackingcaloapp.data.local.dao.WorkoutDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.Workout;

import java.util.List;

/**
 * Repository cho Workout entity.
 * Cung cấp abstraction layer giữa ViewModel và data sources.
 */
public class WorkoutRepository {

    private final WorkoutDao workoutDao;
    private final LiveData<List<Workout>> allWorkouts;

    public WorkoutRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        workoutDao = db.workoutDao();
        allWorkouts = workoutDao.getAllWorkouts();
    }

    // Constructor overload for direct DAO injection
    public WorkoutRepository(WorkoutDao workoutDao) {
        this.workoutDao = workoutDao;
        this.allWorkouts = workoutDao.getAllWorkouts();
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Lấy tất cả bài tập
     */
    public LiveData<List<Workout>> getAllWorkouts() {
        return allWorkouts;
    }
    
    /**
     * Lấy bài tập theo ID
     */
    public Workout getWorkoutById(int workoutId) {
        return workoutDao.getWorkoutById(workoutId);
    }
    
    /**
     * Lấy bài tập theo ID (LiveData)
     */
    public LiveData<Workout> getWorkoutByIdLive(int workoutId) {
        return workoutDao.getWorkoutByIdLive(workoutId);
    }
    
    /**
     * Tìm kiếm bài tập theo tên
     */
    public LiveData<List<Workout>> searchWorkouts(String query) {
        return workoutDao.searchWorkouts(query);
    }
    
    /**
     * Lấy bài tập theo category
     */
    public LiveData<List<Workout>> getWorkoutsByCategory(String category) {
        return workoutDao.getWorkoutsByCategory(category);
    }
    
    /**
     * Lấy bài tập do user tự tạo
     */
    public LiveData<List<Workout>> getCustomWorkouts() {
        return workoutDao.getCustomWorkouts();
    }
    
    /**
     * Lấy danh sách categories
     */
    public LiveData<List<String>> getAllCategories() {
        return workoutDao.getAllCategories();
    }
    
    // ==================== INSERT ====================
    
    /**
     * Thêm bài tập mới
     */
    public void insert(Workout workout) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            workoutDao.insert(workout);
        });
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Cập nhật bài tập
     */
    public void update(Workout workout) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            workoutDao.update(workout);
        });
    }
    
    // ==================== DELETE ====================
    
    /**
     * Xóa bài tập
     */
    public void delete(Workout workout) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            workoutDao.delete(workout);
        });
    }
    
    /**
     * Xóa bài tập theo ID
     */
    public void deleteById(int workoutId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            workoutDao.deleteById(workoutId);
        });
    }
}

