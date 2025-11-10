package com.example.wegoo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private EditText nameEditText, ageEditText, idEditText, phoneEditText, emailEditText, birthdayEditText, addressEditText;
    private Button saveButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Firestore init
        db = FirebaseFirestore.getInstance();

        // Link layout elements
        nameEditText = findViewById(R.id.name_edit_text);
        ageEditText = findViewById(R.id.age_edit_text);
        idEditText = findViewById(R.id.id_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        birthdayEditText = findViewById(R.id.birthday_edit_text);
        addressEditText = findViewById(R.id.address_edit_text);
        saveButton = findViewById(R.id.save_button);

        // Save button listener
        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void saveUserProfile() {
        String name = nameEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();
        String id = idEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String birthday = birthdayEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("age", age);
        user.put("id", id);
        user.put("phone", phone);
        user.put("email", email);
        user.put("birthday", birthday);
        user.put("address", address);

        db.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(UserProfileActivity.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        clearFields();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileActivity.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        nameEditText.setText("");
        ageEditText.setText("");
        idEditText.setText("");
        phoneEditText.setText("");
        emailEditText.setText("");
        birthdayEditText.setText("");
        addressEditText.setText("");
    }
}
