package com.example.trackingcaloapp.model;

import com.example.trackingcaloapp.data.local.entity.Workout;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;

/**
 * Model class kết hợp Workout và WorkoutEntry để hiển thị trong UI.
 * Dùng khi cần hiển thị thông tin đầy đủ của một mục nhập bài tập.
 */
public class WorkoutWithEntry {
    
    private Workout workout;
    private WorkoutEntry workoutEntry;
    
    public WorkoutWithEntry(Workout workout, WorkoutEntry workoutEntry) {
        this.workout = workout;
        this.workoutEntry = workoutEntry;
    }
    
    // ==================== GETTERS ====================
    
    public Workout getWorkout() {
        return workout;
    }
    
    public WorkoutEntry getWorkoutEntry() {
        return workoutEntry;
    }
    
    // ==================== CONVENIENCE METHODS ====================
    
    /**
     * Lấy tên bài tập
     */
    public String getWorkoutName() {
        return workout != null ? workout.getName() : "";
    }
    
    /**
     * Lấy số lượng đã tập
     */
    public float getQuantity() {
        return workoutEntry != null ? workoutEntry.getQuantity() : 0;
    }
    
    /**
     * Lấy đơn vị
     */
    public String getUnit() {
        return workout != null ? workout.getUnit() : "";
    }
    
    /**
     * Lấy tổng calo đốt cháy
     */
    public float getCaloriesBurned() {
        return workoutEntry != null ? workoutEntry.getCaloriesBurned() : 0;
    }
    
    /**
     * Lấy thời gian tập (phút)
     */
    public int getDuration() {
        return workoutEntry != null ? workoutEntry.getDuration() : 0;
    }
    
    /**
     * Lấy category của bài tập
     */
    public String getCategory() {
        return workout != null ? workout.getCategory() : "";
    }
    
    /**
     * Lấy tên hiển thị category
     */
    public String getCategoryDisplayName() {
        return workout != null ? workout.getCategoryDisplayName() : "";
    }
    
    /**
     * Lấy calo per unit của bài tập
     */
    public float getCaloriesPerUnit() {
        return workout != null ? workout.getCaloriesPerUnit() : 0;
    }
    
    /**
     * Lấy timestamp
     */
    public long getDate() {
        return workoutEntry != null ? workoutEntry.getDate() : 0;
    }
    
    /**
     * Lấy ghi chú
     */
    public String getNote() {
        return workoutEntry != null ? workoutEntry.getNote() : "";
    }
    
    /**
     * Lấy ID của entry
     */
    public int getEntryId() {
        return workoutEntry != null ? workoutEntry.getId() : 0;
    }
    
    /**
     * Lấy ID của workout
     */
    public int getWorkoutId() {
        return workout != null ? workout.getId() : 0;
    }
    
    /**
     * Lấy chuỗi hiển thị số lượng + đơn vị
     * VD: "5.0 km", "30 phút", "20 lần"
     */
    public String getQuantityDisplay() {
        if (workout == null || workoutEntry == null) return "";
        float qty = workoutEntry.getQuantity();
        String unit = workout.getUnit();
        
        // Format số: nếu là số nguyên thì không hiển thị phần thập phân
        if (qty == (int) qty) {
            return String.format("%d %s", (int) qty, unit);
        } else {
            return String.format("%.1f %s", qty, unit);
        }
    }
}

