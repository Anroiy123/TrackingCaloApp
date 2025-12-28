package com.example.trackingcaloapp.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity lưu trữ lịch sử cân nặng của người dùng.
 * Mỗi entry đại diện cho một lần ghi nhận cân nặng.
 */
@Entity(
    tableName = "weight_logs",
    indices = {@Index(value = "timestamp", name = "idx_weight_logs_timestamp")}
)
public class WeightLog {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "weight")
    private float weight;  // kg

    @ColumnInfo(name = "timestamp")
    private long timestamp;  // milliseconds since epoch

    @ColumnInfo(name = "note")
    private String note;  // Optional note

    public WeightLog(float weight, long timestamp, String note) {
        this.weight = weight;
        this.timestamp = timestamp;
        this.note = note;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
