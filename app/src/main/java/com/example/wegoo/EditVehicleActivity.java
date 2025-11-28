
package com.example.wegoo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditVehicleActivity extends AppCompatActivity {

    private EditText etVehicleName, etVehicleType, etVehiclePrice, etFuelType,
            etEngineCapacity, etSeatingCapacity, etColor, etTransmission;

    private ImageView imgPreview;
    private Button btnSave, btnUploadPicture;

    private String vehicleId;
    private String imageUrl;
    private Uri selectedImageUri;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private static final String TAG = "EditVehicleActivity";

    // Image picker launcher
    ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imgPreview.setImageURI(selectedImageUri);
                    uploadToCloudinary(selectedImageUri);
                }
            });

    // Permission request launcher
    ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);

        // UI components
        etVehicleName = findViewById(R.id.etVehicleName);
        etVehicleType = findViewById(R.id.etVehicleType);
        etVehiclePrice = findViewById(R.id.etVehiclePrice);
        etFuelType = findViewById(R.id.etFuelType);
        etEngineCapacity = findViewById(R.id.etEngineCapacity);
        etSeatingCapacity = findViewById(R.id.etSeatingCapacity);
        etColor = findViewById(R.id.etColor);
        etTransmission = findViewById(R.id.etTransmission);
        imgPreview = findViewById(R.id.imgPreview);

        btnSave = findViewById(R.id.btnAddVehicle);
        btnUploadPicture = findViewById(R.id.btnUploadPicture);

        // Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        vehicleId = getIntent().getStringExtra("vehicleId");

        loadVehicleData();

        btnUploadPicture.setOnClickListener(v -> checkPermissionAndPickImage());

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadVehicleData() {
        firestore.collection("vehicles").document(vehicleId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {

                        etVehicleName.setText(doc.getString("vehicleName"));
                        etVehicleType.setText(doc.getString("vehicleType"));
                        etVehiclePrice.setText(doc.getString("vehiclePrice"));
                        etFuelType.setText(doc.getString("fuelType"));
                        etEngineCapacity.setText(doc.getString("engineCapacity"));
                        etSeatingCapacity.setText(doc.getString("seatingCapacity"));
                        etColor.setText(doc.getString("color"));
                        etTransmission.setText(doc.getString("transmission"));

                        imageUrl = doc.getString("imageUrl");

                        Glide.with(this)
                                .load(imageUrl)
                                .into(imgPreview);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }

    private void checkPermissionAndPickImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                return;
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                return;
            }
        }

        openImagePicker();
    }

    private void openImagePicker() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        imagePickerLauncher.launch(i);
    }

    private void uploadToCloudinary(Uri uri) {

        MediaManager.get().upload(uri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(EditVehicleActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        imageUrl = resultData.get("secure_url").toString();
                        Log.d(TAG, "Cloudinary URL: " + imageUrl);

                        Glide.with(EditVehicleActivity.this)
                                .load(imageUrl)
                                .into(imgPreview);

                        Toast.makeText(EditVehicleActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(EditVehicleActivity.this,
                                "Upload error: " + error.getDescription(), Toast.LENGTH_LONG).show();

                        Log.e(TAG, "Cloudinary error: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Rescheduled: " + error.getDescription());
                    }
                })
                .dispatch();
    }

    private void saveChanges() {

        Map<String, Object> updated = new HashMap<>();
        updated.put("vehicleName", etVehicleName.getText().toString());
        updated.put("vehicleType", etVehicleType.getText().toString());
        updated.put("vehiclePrice", etVehiclePrice.getText().toString());
        updated.put("fuelType", etFuelType.getText().toString());
        updated.put("engineCapacity", etEngineCapacity.getText().toString());
        updated.put("seatingCapacity", etSeatingCapacity.getText().toString());
        updated.put("color", etColor.getText().toString());
        updated.put("transmission", etTransmission.getText().toString());
        updated.put("imageUrl", imageUrl);

        firestore.collection("vehicles")
                .document(vehicleId)
                .update(updated)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Vehicle updated!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show());
    }
}

