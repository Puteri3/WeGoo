package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProviderLoginActivity extends AppCompatActivity {

    private EditText inputProviderEmail, inputProviderPassword;
    private Button btnProviderLogin;
    private TextView tvBackToUser, tvForgotPassword;
    private FirebaseAuth mAuth;

    // Default Provider Account (Registered manually in Firebase Authentication)
    private static final String PROVIDER_EMAIL = "msngrouplegacy@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_login);

        // 🔹 Initialize UI components
        inputProviderEmail = findViewById(R.id.inputProviderEmail);
        inputProviderPassword = findViewById(R.id.inputProviderPassword);
        btnProviderLogin = findViewById(R.id.btnProviderLogin);
        tvBackToUser = findViewById(R.id.tvBackToUser);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // 🔹 Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 🔹 Forgot Password click
//        tvForgotPassword.setOnClickListener(v -> {
//            startActivity(new Intent(ProviderLoginActivity.this, ForgotPasswordActivity.class));
//        });

        // 🟢 Handle Provider Login
        btnProviderLogin.setOnClickListener(v -> {
            String email = inputProviderEmail.getText().toString().trim();
            String password = inputProviderPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && email.equals(PROVIDER_EMAIL)) {

                                // ✅ Custom Welcome Message
                                Toast.makeText(this, "✅ Welcome MSN Group Legacy Admin!", Toast.LENGTH_LONG).show();

                                // 📌 Nanti bila dah ada homepage, ganti komen ni dengan redirect
                                startActivity(new Intent(this, ProviderHomepageActivity.class));
                                 finish();

                            } else {
                                Toast.makeText(this, "Unauthorized access. Only provider allowed.", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                        } else {
                            Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // 🟣 Back to user login
        tvBackToUser.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
