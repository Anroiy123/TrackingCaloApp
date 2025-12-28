package com.example.trackingcaloapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.entity.User;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.data.repository.UserRepository;
import com.example.trackingcaloapp.ui.main.MainActivity;

import java.util.concurrent.ExecutionException;

/**
 * Màn hình đăng nhập của ứng dụng.
 * Sử dụng Room Database để xác thực và SharedPreferences để lưu trạng thái.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView tvRegisterLink;

    private UserPreferences userPreferences;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        userPreferences = new UserPreferences(this);
        userRepository = new UserRepository(getApplication());

        // Check if already logged in
        if (checkLogin()) {
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegisterLink.setOnClickListener(v -> navigateToRegister());
    }

    /**
     * Kiểm tra xem người dùng đã đăng nhập chưa.
     * Nếu đã đăng nhập, chuyển đến MainActivity.
     * @return true nếu đã chuyển đến MainActivity
     */
    private boolean checkLogin() {
        if (userPreferences.isLoggedIn()) {
            navigateToMain();
            return true;
        }
        return false;
    }

    /**
     * Thực hiện đăng nhập với thông tin người dùng nhập.
     */
    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (username.isEmpty()) {
            etUsername.setError(getString(R.string.error_username_required));
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.error_password_required));
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        try {
            User user = userRepository.login(username, password).get();

            if (user != null) {
                // Valid credentials
                if (cbRememberMe.isChecked()) {
                    userPreferences.setLoggedIn(true);
                }
                userPreferences.setCurrentUserId(user.getId());
                userPreferences.setLoginUsername(username);
                navigateToMain();
            } else {
                // Invalid credentials
                Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
            }
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
        }
    }

    /**
     * Chuyển đến RegisterActivity.
     */
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Chuyển đến MainActivity và kết thúc LoginActivity.
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
