package com.example.trackingcaloapp.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.trackingcaloapp.data.local.entity.Workout;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;

/**
 * POJO class để Room map kết quả JOIN giữa WorkoutEntry và Workout.
 * Sử dụng @Embedded và @Relation để Room tự động JOIN.
 */
public class WorkoutEntryWithWorkout {
    
    @Embedded
    private WorkoutEntry workoutEntry;
    
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "id"
    )
    private Workout workout;
    
    public WorkoutEntryWithWorkout() {}
    
    public WorkoutEntry getWorkoutEntry() {
        return workoutEntry;
    }
    
    public void setWorkoutEntry(WorkoutEntry workoutEntry) {
        this.workoutEntry = workoutEntry;
    }
    
    public Workout getWorkout() {
        return workout;
    }
    
    public void setWorkout(Workout workout) {
        this.workout = workout;
    }
    
    /**
     * Chuyển đổi sang WorkoutWithEntry để sử dụng trong UI
     */
    public WorkoutWithEntry toWorkoutWithEntry() {
        return new WorkoutWithEntry(workout, workoutEntry);
    }
}

