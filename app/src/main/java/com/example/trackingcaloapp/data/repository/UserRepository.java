package com.example.trackingcaloapp.data.repository;

import android.app.Application;

import com.example.trackingcaloapp.data.local.dao.UserDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.User;
import com.example.trackingcaloapp.util.PasswordUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Repository cho User entity.
 * Cung cấp abstraction layer giữa UI và data sources.
 */
public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executor;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Đăng ký tài khoản mới
     * @return userId nếu thành công, -1 nếu username đã tồn tại
     */
    public Future<Long> register(String username, String password) {
        return executor.submit(() -> {
            if (userDao.countByUsername(username) > 0) {
                return -1L; // Username đã tồn tại
            }
            String passwordHash = PasswordUtils.hashPassword(password);
            User user = new User(username, passwordHash);
            return userDao.insert(user);
        });
    }

    /**
     * Đăng nhập
     * @return User nếu thành công, null nếu thất bại
     */
    public Future<User> login(String username, String password) {
        return executor.submit(() -> {
            String passwordHash = PasswordUtils.hashPassword(password);
            if (userDao.validateCredentials(username, passwordHash)) {
                return userDao.getUserByUsername(username);
            }
            return null;
        });
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public Future<Boolean> isUsernameExists(String username) {
        return executor.submit(() -> userDao.countByUsername(username) > 0);
    }

    /**
     * Lấy user theo ID
     */
    public Future<User> getUserById(int userId) {
        return executor.submit(() -> userDao.getUserById(userId));
    }
}
