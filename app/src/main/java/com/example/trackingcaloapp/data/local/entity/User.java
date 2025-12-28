package com.example.trackingcaloapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện cho tài khoản người dùng trong cơ sở dữ liệu.
 * Lưu trữ thông tin đăng nhập local.
 */
@Entity(
    tableName = "users",
    indices = {@Index(value = "username", unique = true)}
)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;
    private String passwordHash;
    private long createdAt;

    public User() {}

    @Ignore
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
