package com.pigeonpost.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeonpost.android.R;
import com.pigeonpost.android.network.RetrofitClient;
import com.pigeonpost.android.network.dto.AuthResponse;
import com.pigeonpost.android.network.dto.LoginRequest;
import com.pigeonpost.android.security.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private MaterialButton loginButton;
    private ProgressBar loginProgress;

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenManager = new TokenManager(this);

        if (tokenManager.hasAccessToken()) {
            openMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editLoginEmail);
        editPassword = findViewById(R.id.editLoginPassword);
        loginButton = findViewById(R.id.btnLogin);
        loginProgress = findViewById(R.id.loginProgress);

        loginButton.setOnClickListener(view -> attemptLogin());
    }

    private void attemptLogin() {
        String email = editEmail.getText() == null
                ? ""
                : editEmail.getText().toString().trim();

        String password = editPassword.getText() == null
                ? ""
                : editPassword.getText().toString();

        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Password is required");
            return;
        }

        setLoading(true);

        LoginRequest loginRequest = new LoginRequest(
                email,
                password
        );

        RetrofitClient.getApiService()
                .login(loginRequest)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(
                            Call<AuthResponse> call,
                            Response<AuthResponse> response
                    ) {
                        setLoading(false);

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            if (response.code() == 401) {
                                Toast.makeText(
                                        LoginActivity.this,
                                        "Incorrect email or password.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        LoginActivity.this,
                                        "Login failed: " + response.code(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            return;
                        }

                        AuthResponse authResponse = response.body();

                        if (authResponse.getAccessToken() == null
                                || authResponse.getRefreshToken() == null) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "The server returned an invalid response.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        tokenManager.saveTokens(
                                authResponse.getAccessToken(),
                                authResponse.getRefreshToken(),
                                authResponse.getTokenType()
                        );

                        openMainActivity();
                    }

                    @Override
                    public void onFailure(
                            Call<AuthResponse> call,
                            Throwable throwable
                    ) {
                        setLoading(false);

                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to reach the server.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        loginProgress.setVisibility(
                isLoading ? View.VISIBLE : View.GONE
        );

        loginButton.setEnabled(!isLoading);
        editEmail.setEnabled(!isLoading);
        editPassword.setEnabled(!isLoading);
    }

    private void openMainActivity() {
        Intent intent = new Intent(
                LoginActivity.this,
                MainActivity.class
        );

        startActivity(intent);
        finish();
    }
}