package com.example.trackingcaloapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;

import java.util.List;

/**
 * Data Access Object cho WorkoutEntry entity.
 * Cung cấp các phương thức truy vấn nhật ký tập luyện.
 */
@Dao
public interface WorkoutEntryDao {
    
    // ==================== INSERT ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WorkoutEntry workoutEntry);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<WorkoutEntry> workoutEntries);
    
    // ==================== UPDATE ====================
    
    @Update
    void update(WorkoutEntry workoutEntry);
    
    // ==================== DELETE ====================
    
    @Delete
    void delete(WorkoutEntry workoutEntry);
    
    @Query("DELETE FROM workout_entries WHERE id = :entryId")
    void deleteById(int entryId);
    
    @Query("DELETE FROM workout_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    void deleteEntriesByDate(long startOfDay, long endOfDay);
    
    // ==================== QUERY ====================
    
    /**
     * Lấy tất cả workout entries
     */
    @Query("SELECT * FROM workout_entries ORDER BY date DESC")
    LiveData<List<WorkoutEntry>> getAllEntries();
    
    /**
     * Lấy workout entry theo ID
     */
    @Query("SELECT * FROM workout_entries WHERE id = :entryId")
    WorkoutEntry getEntryById(int entryId);
    
    /**
     * Lấy các entries trong một ngày cụ thể
     * @param startOfDay Timestamp đầu ngày (00:00:00)
     * @param endOfDay Timestamp cuối ngày (23:59:59)
     */
    @Query("SELECT * FROM workout_entries WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date ASC")
    LiveData<List<WorkoutEntry>> getEntriesByDate(long startOfDay, long endOfDay);
    
    /**
     * Lấy các entries trong một ngày (không LiveData)
     */
    @Query("SELECT * FROM workout_entries WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date ASC")
    List<WorkoutEntry> getEntriesByDateSync(long startOfDay, long endOfDay);
    
    // ==================== AGGREGATION ====================
    
    /**
     * Tính tổng calo đốt cháy trong một ngày
     */
    @Query("SELECT COALESCE(SUM(caloriesBurned), 0) FROM workout_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<Float> getTotalCaloriesBurnedByDate(long startOfDay, long endOfDay);
    
    /**
     * Tính tổng calo đốt cháy trong một ngày (không LiveData)
     */
    @Query("SELECT COALESCE(SUM(caloriesBurned), 0) FROM workout_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    float getTotalCaloriesBurnedByDateSync(long startOfDay, long endOfDay);
    
    /**
     * Tính tổng thời gian tập trong một ngày (phút)
     */
    @Query("SELECT COALESCE(SUM(duration), 0) FROM workout_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<Integer> getTotalDurationByDate(long startOfDay, long endOfDay);
    
    /**
     * Đếm số entries trong một ngày
     */
    @Query("SELECT COUNT(*) FROM workout_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    int getEntryCountByDate(long startOfDay, long endOfDay);
    
    /**
     * Lấy entries theo category của workout trong một ngày
     */
    @Query("SELECT we.* FROM workout_entries we " +
           "INNER JOIN workouts w ON we.workoutId = w.id " +
           "WHERE we.date BETWEEN :startOfDay AND :endOfDay AND w.category = :category " +
           "ORDER BY we.date ASC")
    LiveData<List<WorkoutEntry>> getEntriesByDateAndCategory(long startOfDay, long endOfDay, String category);
}

