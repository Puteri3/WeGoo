package com.example.wegoo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserSettingsActivity extends AppCompatActivity {

    private TextView tvUserId, tvUserEmail;
    private EditText etUserName;
    private Button btnSaveChanges;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        tvUserId = findViewById(R.id.tvUserId);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        etUserName = findViewById(R.id.etUserName);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            loadUserProfile();
        }

        btnSaveChanges.setOnClickListener(v -> saveUserChanges());
    }

    private void loadUserProfile() {
        DocumentReference docRef = mStore.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("userName");
                String email = documentSnapshot.getString("email");

                // Mask user ID
                if (userId.length() > 4) {
                    tvUserId.setText(userId.substring(0, 4) + "****");
                } else {
                    tvUserId.setText(userId);
                }
                
                tvUserEmail.setText(email);
                etUserName.setText(name);

            } else {
                Toast.makeText(UserSettingsActivity.this, "Profile not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(UserSettingsActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserChanges() {
        String newName = etUserName.getText().toString().trim();

        if (newName.isEmpty()) {
            etUserName.setError("Name cannot be empty");
            etUserName.requestFocus();
            return;
        }

        DocumentReference docRef = mStore.collection("Users").document(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("userName", newName);

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserSettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the previous screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserSettingsActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }
}
