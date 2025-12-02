package com.example.trackingcaloapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trackingcaloapp.data.local.entity.Workout;

import java.util.List;

/**
 * Data Access Object cho Workout entity.
 * Cung cấp các phương thức truy vấn cơ sở dữ liệu bài tập.
 */
@Dao
public interface WorkoutDao {
    
    // ==================== INSERT ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Workout workout);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Workout> workouts);
    
    // ==================== UPDATE ====================
    
    @Update
    void update(Workout workout);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(Workout workout);
    
    @Query("DELETE FROM workouts WHERE id = :workoutId")
    void deleteById(int workoutId);
    
    @Query("DELETE FROM workouts WHERE isCustom = 1")
    void deleteAllCustomWorkouts();
    
    // ==================== QUERY ====================
    
    /**
     * Lấy tất cả bài tập, sắp xếp theo tên
     */
    @Query("SELECT * FROM workouts ORDER BY name ASC")
    LiveData<List<Workout>> getAllWorkouts();
    
    /**
     * Lấy tất cả bài tập (không LiveData - dùng cho background thread)
     */
    @Query("SELECT * FROM workouts ORDER BY name ASC")
    List<Workout> getAllWorkoutsSync();
    
    /**
     * Lấy bài tập theo ID
     */
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    Workout getWorkoutById(int workoutId);
    
    /**
     * Lấy bài tập theo ID (LiveData)
     */
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    LiveData<Workout> getWorkoutByIdLive(int workoutId);
    
    /**
     * Tìm kiếm bài tập theo tên
     */
    @Query("SELECT * FROM workouts WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    LiveData<List<Workout>> searchWorkouts(String searchQuery);
    
    /**
     * Tìm kiếm bài tập theo tên (không LiveData)
     */
    @Query("SELECT * FROM workouts WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    List<Workout> searchWorkoutsSync(String searchQuery);
    
    /**
     * Lấy bài tập theo category
     */
    @Query("SELECT * FROM workouts WHERE category = :category ORDER BY name ASC")
    LiveData<List<Workout>> getWorkoutsByCategory(String category);
    
    /**
     * Lấy tất cả bài tập do user tự tạo
     */
    @Query("SELECT * FROM workouts WHERE isCustom = 1 ORDER BY name ASC")
    LiveData<List<Workout>> getCustomWorkouts();
    
    /**
     * Lấy tất cả bài tập có sẵn trong app
     */
    @Query("SELECT * FROM workouts WHERE isCustom = 0 ORDER BY name ASC")
    LiveData<List<Workout>> getDefaultWorkouts();
    
    /**
     * Đếm số lượng bài tập trong database
     */
    @Query("SELECT COUNT(*) FROM workouts")
    int getWorkoutCount();
    
    /**
     * Lấy danh sách các category có trong database
     */
    @Query("SELECT DISTINCT category FROM workouts ORDER BY category ASC")
    LiveData<List<String>> getAllCategories();
}

