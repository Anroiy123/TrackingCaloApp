package com.example.trackingcaloapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
 * Màn hình đăng ký tài khoản mới.
 * Lưu thông tin user vào Room Database local.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;

    private UserRepository userRepository;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        userRepository = new UserRepository(getApplication());
        userPreferences = new UserPreferences(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLoginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validation
        if (username.isEmpty()) {
            etUsername.setError(getString(R.string.error_username_required));
            etUsername.requestFocus();
            return;
        }

        if (username.length() < 3) {
            etUsername.setError(getString(R.string.error_username_min_length));
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.error_password_required));
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError(getString(R.string.error_password_min_length));
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.error_password_mismatch));
            etConfirmPassword.requestFocus();
            return;
        }

        // Disable button during registration
        btnRegister.setEnabled(false);

        try {
            Long userId = userRepository.register(username, password).get();

            if (userId == -1L) {
                Toast.makeText(this, R.string.error_username_exists, Toast.LENGTH_SHORT).show();
                btnRegister.setEnabled(true);
                return;
            }

            // Registration successful
            userPreferences.setLoggedIn(true);
            userPreferences.setCurrentUserId(userId.intValue());
            userPreferences.setLoginUsername(username);

            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
            navigateToMain();

        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, R.string.error_register_failed, Toast.LENGTH_SHORT).show();
            btnRegister.setEnabled(true);
        }
    }

    private void navigateToLogin() {
        finish(); // Quay lại LoginActivity
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
