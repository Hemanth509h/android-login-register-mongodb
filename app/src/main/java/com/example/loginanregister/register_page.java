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

public class register_page extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private EditText confirmPasswordInput;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);

        // Initialize views
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input); // Initialize email input
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        Button registerButton = findViewById(R.id.register_button);
        TextView signin_button = findViewById(R.id.signin_button);
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar

        // Set register button click listener
        registerButton.setOnClickListener(v -> performRegistration());

        signin_button.setOnClickListener(v -> {
            Intent intent = new Intent(register_page.this, login_page.class);
            startActivity(intent);
        });
    }

    private void performRegistration() {
        // Get the entered username, email, and password
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim(); // Get email
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://353e2e14-0c27-4977-9d62-97e77239b581-00-1yz54g7nxvlrm.pike.replit.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create API service
        ApiService apiService = retrofit.create(ApiService.class);

        // Create registration request object
        RegisterRequest registerRequest = new RegisterRequest(username, email, password); // Include email

        // Make the API call
        Call<RegisterResponse> call = apiService.register(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {

                // Hide the loading indicator
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();
                    if ("success".equals(registerResponse.getStatus())) {
                        Toast.makeText(register_page.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(register_page.this, login_page.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(register_page.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(register_page.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

                // Hide the loading indicator
                progressBar.setVisibility(View.GONE);
                Toast.makeText(register_page.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Retrofit API interface
    public interface ApiService {
        @POST("/register")
        Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
    }

    // Registration request model
    public class RegisterRequest {
        private String username;
        private String email; // Add email field
        private String password;

        public RegisterRequest(String username, String email, String password) {
            this.username = username;
            this.email = email; // Initialize email
            this.password = password;
        }
    }

    // Registration response model
    public class RegisterResponse {
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
