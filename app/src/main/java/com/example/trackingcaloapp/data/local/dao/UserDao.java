package com.example.trackingcaloapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.trackingcaloapp.data.local.entity.User;

/**
 * DAO cho User entity.
 * Cung cấp các phương thức truy cập dữ liệu người dùng.
 */
@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countByUsername(String username);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username AND passwordHash = :passwordHash)")
    boolean validateCredentials(String username, String passwordHash);
}
