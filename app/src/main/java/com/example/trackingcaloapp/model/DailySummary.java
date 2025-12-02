package com.example.trackingcaloapp.model;

import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class đại diện cho tổng hợp dữ liệu trong một ngày.
 * Bao gồm calo ăn vào, calo đốt cháy, và các thông tin dinh dưỡng.
 */
public class DailySummary {
    
    private long date;                          // Timestamp của ngày
    private int calorieGoal;                    // Mục tiêu calo/ngày
    
    private float caloriesConsumed;             // Tổng calo ăn vào
    private float caloriesBurned;               // Tổng calo đốt cháy (workout)
    
    private float proteinTotal;                 // Tổng protein (g)
    private float carbsTotal;                   // Tổng carbs (g)
    private float fatTotal;                     // Tổng fat (g)
    
    private List<FoodEntry> foodEntries;        // Danh sách food entries
    private List<WorkoutEntry> workoutEntries;  // Danh sách workout entries
    
    // Constructor mặc định
    public DailySummary() {
        this.foodEntries = new ArrayList<>();
        this.workoutEntries = new ArrayList<>();
    }
    
    // Constructor với date và goal
    public DailySummary(long date, int calorieGoal) {
        this();
        this.date = date;
        this.calorieGoal = calorieGoal;
    }
    
    // ==================== CALCULATED PROPERTIES ====================
    
    /**
     * Tính calo NET = ăn vào - đốt cháy
     */
    public float getNetCalories() {
        return caloriesConsumed - caloriesBurned;
    }
    
    /**
     * Tính calo còn lại có thể ăn
     */
    public float getRemainingCalories() {
        return calorieGoal - getNetCalories();
    }
    
    /**
     * Tính phần trăm hoàn thành mục tiêu
     */
    public int getProgressPercentage() {
        if (calorieGoal <= 0) return 0;
        return Math.round((getNetCalories() / calorieGoal) * 100);
    }
    
    /**
     * Kiểm tra xem đã vượt mục tiêu chưa
     */
    public boolean isOverGoal() {
        return getNetCalories() > calorieGoal;
    }
    
    /**
     * Lấy số lượng bữa ăn đã ghi
     */
    public int getFoodEntryCount() {
        return foodEntries != null ? foodEntries.size() : 0;
    }
    
    /**
     * Lấy số lượng bài tập đã ghi
     */
    public int getWorkoutEntryCount() {
        return workoutEntries != null ? workoutEntries.size() : 0;
    }
    
    /**
     * Tính tổng thời gian tập luyện (phút)
     */
    public int getTotalWorkoutDuration() {
        if (workoutEntries == null) return 0;
        int total = 0;
        for (WorkoutEntry entry : workoutEntries) {
            total += entry.getDuration();
        }
        return total;
    }
    
    // ==================== GETTERS AND SETTERS ====================
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public int getCalorieGoal() {
        return calorieGoal;
    }
    
    public void setCalorieGoal(int calorieGoal) {
        this.calorieGoal = calorieGoal;
    }
    
    public float getCaloriesConsumed() {
        return caloriesConsumed;
    }
    
    public void setCaloriesConsumed(float caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }
    
    public float getCaloriesBurned() {
        return caloriesBurned;
    }
    
    public void setCaloriesBurned(float caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
    
    public float getProteinTotal() {
        return proteinTotal;
    }
    
    public void setProteinTotal(float proteinTotal) {
        this.proteinTotal = proteinTotal;
    }
    
    public float getCarbsTotal() {
        return carbsTotal;
    }
    
    public void setCarbsTotal(float carbsTotal) {
        this.carbsTotal = carbsTotal;
    }
    
    public float getFatTotal() {
        return fatTotal;
    }
    
    public void setFatTotal(float fatTotal) {
        this.fatTotal = fatTotal;
    }
    
    public List<FoodEntry> getFoodEntries() {
        return foodEntries;
    }
    
    public void setFoodEntries(List<FoodEntry> foodEntries) {
        this.foodEntries = foodEntries;
    }
    
    public List<WorkoutEntry> getWorkoutEntries() {
        return workoutEntries;
    }
    
    public void setWorkoutEntries(List<WorkoutEntry> workoutEntries) {
        this.workoutEntries = workoutEntries;
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Thêm food entry và cập nhật totals
     */
    public void addFoodEntry(FoodEntry entry) {
        if (foodEntries == null) {
            foodEntries = new ArrayList<>();
        }
        foodEntries.add(entry);
        caloriesConsumed += entry.getTotalCalories();
        proteinTotal += entry.getTotalProtein();
        carbsTotal += entry.getTotalCarbs();
        fatTotal += entry.getTotalFat();
    }
    
    /**
     * Thêm workout entry và cập nhật totals
     */
    public void addWorkoutEntry(WorkoutEntry entry) {
        if (workoutEntries == null) {
            workoutEntries = new ArrayList<>();
        }
        workoutEntries.add(entry);
        caloriesBurned += entry.getCaloriesBurned();
    }
    
    /**
     * Reset tất cả dữ liệu
     */
    public void reset() {
        caloriesConsumed = 0;
        caloriesBurned = 0;
        proteinTotal = 0;
        carbsTotal = 0;
        fatTotal = 0;
        if (foodEntries != null) foodEntries.clear();
        if (workoutEntries != null) workoutEntries.clear();
    }
}

