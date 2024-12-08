package com.example.loginanregister;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class login_page extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private ProgressBar progressBar; // ProgressBar for loading

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Initialize views
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        Button loginbutton = findViewById(R.id.loginbutton);
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar
        TextView signup_button = findViewById(R.id.signup_button);

        // Set login button click listener
        loginbutton.setOnClickListener(v -> performLogin());

        signup_button.setOnClickListener(v -> {
            Intent intent = new Intent(login_page.this, register_page.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        // Get the entered username and password
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://353e2e14-0c27-4977-9d62-97e77239b581-00-1yz54g7nxvlrm.pike.replit.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create API service
        ApiService apiService = retrofit.create(ApiService.class);

        // Create login request object
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Make the API call
        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                // Hide the loading indicator
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if ("success".equals(loginResponse.getStatus())) {
                        Toast.makeText(login_page.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(login_page.this, after_login.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(login_page.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(login_page.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Hide the loading indicator
                progressBar.setVisibility(View.GONE);

                Toast.makeText(login_page.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openSignUp(View view) {
        Intent intent = new Intent(login_page.this, flashscreen.class);
        startActivity(intent);
        finish();
    }

    // Retrofit API interface
    public interface ApiService {
        @POST("/login")
        Call<LoginResponse> login(@Body LoginRequest loginRequest);
    }

    // Login request model
    public class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    // Login response model
    public class LoginResponse {
        private String message;
        private String status;

        public String getMessage() {
            return message;
        }

        public String getStatus() {
            return status;
        }
    }
}
