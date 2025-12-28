package com.example.trackingcaloapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.trackingcaloapp.data.local.entity.WeightLog;

import java.util.List;

/**
 * DAO cho WeightLog entity.
 * Cung cấp các phương thức truy vấn lịch sử cân nặng.
 */
@Dao
public interface WeightLogDao {

    @Insert
    long insert(WeightLog log);

    @Query("SELECT * FROM weight_logs ORDER BY timestamp DESC")
    LiveData<List<WeightLog>> getAllLogs();

    @Query("SELECT * FROM weight_logs WHERE timestamp >= :startTime ORDER BY timestamp ASC")
    LiveData<List<WeightLog>> getLogsSince(long startTime);

    @Query("SELECT * FROM weight_logs ORDER BY timestamp DESC LIMIT 1")
    LiveData<WeightLog> getLatestLog();

    @Query("SELECT * FROM weight_logs ORDER BY timestamp DESC LIMIT 1")
    WeightLog getLatestLogSync();

    @Delete
    void delete(WeightLog log);

    @Query("SELECT COUNT(*) FROM weight_logs")
    int getLogCount();
}
