package com.example.wegoo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProviderProfileSettingsActivity extends AppCompatActivity {

    private EditText etProviderName, etProviderEmail, etProviderAddress, etProviderPhone;
    private Button btnUpdateProfile, btnChangePassword, btnChangePhoto;
    private ImageView profileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private StorageReference storageReference;

    private Uri imageUri;

    // Handles image picker result
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Glide.with(this).load(imageUri).into(profileImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_profile_settings);

        // Initialize views
        etProviderName = findViewById(R.id.etProviderName);
        etProviderEmail = findViewById(R.id.etProviderEmail);
        etProviderAddress = findViewById(R.id.etProviderAddress);
        etProviderPhone = findViewById(R.id.etProviderPhone);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        profileImage = findViewById(R.id.profileImage);

        // Firebase setup
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Load existing profile data
        if (currentUser != null) {
            loadProviderProfile();
        }

        // Change Photo button click
        btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        // Update profile button click
        btnUpdateProfile.setOnClickListener(v -> updateProfile());

        // Change password button click
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void loadProviderProfile() {
        mDatabase.child("providers").child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Provider provider = snapshot.getValue(Provider.class);
                            if (provider != null) {
                                etProviderName.setText(provider.getName());
                                etProviderEmail.setText(provider.getEmail());
                                etProviderAddress.setText(provider.getAddress());
                                etProviderPhone.setText(provider.getPhone());

                                if (snapshot.hasChild("profileImageUrl")) {
                                    String imageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        Glide.with(ProviderProfileSettingsActivity.this)
                                                .load(imageUrl)
                                                .into(profileImage);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProviderProfileSettingsActivity.this,
                                "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfile() {
        String name = etProviderName.getText().toString().trim();
        String email = etProviderEmail.getText().toString().trim();
        String address = etProviderAddress.getText().toString().trim();
        String phone = etProviderPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // If new photo selected → upload to Firebase Storage
        if (imageUri != null) {
            final StorageReference fileRef = storageReference.child("profile_images/" + currentUser.getUid() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                mDatabase.child("providers")
                                        .child(currentUser.getUid())
                                        .child("profileImageUrl")
                                        .setValue(uri.toString());
                                Toast.makeText(ProviderProfileSettingsActivity.this,
                                        "Profile image updated", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(ProviderProfileSettingsActivity.this,
                                    "Failed to upload image", Toast.LENGTH_SHORT).show());
        }

        // Update email in Firebase Auth
        currentUser.updateEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update other profile data
                DatabaseReference providerRef = mDatabase.child("providers").child(currentUser.getUid());
                providerRef.child("name").setValue(name);
                providerRef.child("email").setValue(email);
                providerRef.child("address").setValue(address);
                providerRef.child("phone").setValue(phone);

                Toast.makeText(ProviderProfileSettingsActivity.this,
                        "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProviderProfileSettingsActivity.this,
                        "Failed to update email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword() {
        String email = etProviderEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProviderProfileSettingsActivity.this,
                                "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProviderProfileSettingsActivity.this,
                                "Failed to send reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
