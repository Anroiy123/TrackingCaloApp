package com.example.trackingcaloapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.preferences.UserPreferences;
import com.example.trackingcaloapp.ui.main.MainActivity;

/**
 * Màn hình đăng nhập của ứng dụng.
 * Sử dụng SharedPreferences để lưu trạng thái đăng nhập.
 */
public class LoginActivity extends AppCompatActivity {

    // Hardcoded credentials (for demo purposes)
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "123456";

    private EditText etUsername, etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        userPreferences = new UserPreferences(this);

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
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
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

        if (username.equals(VALID_USERNAME) && password.equals(VALID_PASSWORD)) {
            // Valid credentials
            if (cbRememberMe.isChecked()) {
                userPreferences.setLoggedIn(true);
            }
            navigateToMain();
        } else {
            // Invalid credentials
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
        }
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
