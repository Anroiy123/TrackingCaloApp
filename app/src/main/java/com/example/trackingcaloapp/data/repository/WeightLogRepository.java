package com.example.trackingcaloapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackingcaloapp.data.local.dao.WeightLogDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.WeightLog;

import java.util.List;

/**
 * Repository cho WeightLog entity.
 * Cung cấp abstraction layer giữa ViewModel và data sources.
 */
public class WeightLogRepository {

    private final WeightLogDao weightLogDao;

    public WeightLogRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        weightLogDao = db.weightLogDao();
    }

    /**
     * Insert một weight log mới
     */
    public void insert(WeightLog log) {
        AppDatabase.databaseWriteExecutor.execute(() -> weightLogDao.insert(log));
    }

    /**
     * Lấy tất cả weight logs, sắp xếp theo thời gian giảm dần
     */
    public LiveData<List<WeightLog>> getAllLogs() {
        return weightLogDao.getAllLogs();
    }

    /**
     * Lấy weight logs trong 30 ngày gần nhất
     */
    public LiveData<List<WeightLog>> getLast30DaysLogs() {
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        return weightLogDao.getLogsSince(thirtyDaysAgo);
    }

    /**
     * Lấy weight log mới nhất
     */
    public LiveData<WeightLog> getLatestLog() {
        return weightLogDao.getLatestLog();
    }

    /**
     * Xóa một weight log
     */
    public void delete(WeightLog log) {
        AppDatabase.databaseWriteExecutor.execute(() -> weightLogDao.delete(log));
    }
}
