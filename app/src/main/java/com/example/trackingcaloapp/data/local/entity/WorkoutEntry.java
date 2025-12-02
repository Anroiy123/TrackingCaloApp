package com.example.trackingcaloapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện cho một mục nhập bài tập trong nhật ký tập luyện.
 * Liên kết với Workout entity thông qua workoutId.
 */
@Entity(
    tableName = "workout_entries",
    foreignKeys = @ForeignKey(
        entity = Workout.class,
        parentColumns = "id",
        childColumns = "workoutId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("workoutId"), @Index("date")}
)
public class WorkoutEntry {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int workoutId;         // Foreign key liên kết với Workout
    private float quantity;        // Số lượng (phút, km, hoặc lần)
    private int duration;          // Thời gian tập (phút) - optional, để tracking
    private long date;             // Timestamp (milliseconds) - ngày tập
    private float caloriesBurned;  // Calo đã đốt = workout.caloriesPerUnit * quantity
    private String note;           // Ghi chú (optional)

    // Default constructor for Room
    public WorkoutEntry() {}

    // Constructor
    @Ignore
    public WorkoutEntry(int workoutId, float quantity, int duration, long date, float caloriesBurned, String note) {
        this.workoutId = workoutId;
        this.quantity = quantity;
        this.duration = duration;
        this.date = date;
        this.caloriesBurned = caloriesBurned;
        this.note = note;
    }

    // Constructor không có note
    @Ignore
    public WorkoutEntry(int workoutId, float quantity, int duration, long date, float caloriesBurned) {
        this(workoutId, quantity, duration, date, caloriesBurned, null);
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getWorkoutId() {
        return workoutId;
    }
    
    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }
    
    public float getQuantity() {
        return quantity;
    }
    
    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public float getCaloriesBurned() {
        return caloriesBurned;
    }
    
    public void setCaloriesBurned(float caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
}

