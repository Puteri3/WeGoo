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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateVehicleActivity extends AppCompatActivity {

    private static final String TAG = "UpdateVehicleActivity";

    private FirebaseFirestore firestore;

    private EditText etVehicleName, etVehicleType, etVehiclePrice, etFuelType, etEngineCapacity,
            etSeatingCapacity, etColor, etTransmission;
    private ImageView imgPreview;
    private Uri selectedImageUri;
    private String vehicleId;
    private Vehicle currentVehicle;
    private String currentProviderId;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) openImagePicker();
                else Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imgPreview.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_vehicle);

        firestore = FirebaseFirestore.getInstance();
        currentProviderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        etVehicleName = findViewById(R.id.etVehicleName);
        etVehicleType = findViewById(R.id.etVehicleType);
        etVehiclePrice = findViewById(R.id.etVehiclePrice);
        etFuelType = findViewById(R.id.etFuelType);
        etEngineCapacity = findViewById(R.id.etEngineCapacity);
        etSeatingCapacity = findViewById(R.id.etSeatingCapacity);
        etColor = findViewById(R.id.etColor);
        etTransmission = findViewById(R.id.etTransmission);
        imgPreview = findViewById(R.id.imgPreview);
        Button btnSaveVehicle = findViewById(R.id.btnAddVehicle);
        Button btnUploadPicture = findViewById(R.id.btnUploadPicture);

        btnUploadPicture.setOnClickListener(v -> checkPermissionAndOpenPicker());

        vehicleId = getIntent().getStringExtra("vehicleId");

        if (vehicleId != null && !vehicleId.isEmpty()) {
            // EDIT MODE
            setTitle("Edit Vehicle");
            loadVehicleData();
            btnSaveVehicle.setText("Save Changes");
        } else {
            // ADD MODE
            setTitle("Add New Vehicle");
            btnSaveVehicle.setText("Add Vehicle");
        }

        btnSaveVehicle.setOnClickListener(v -> saveVehicle());
    }

    private void checkPermissionAndOpenPicker() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else requestPermissionLauncher.launch(permission);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void loadVehicleData() {
        firestore.collection("vehicles").document(vehicleId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentVehicle = doc.toObject(Vehicle.class);
                        if (currentVehicle != null) {
                            etVehicleName.setText(currentVehicle.getVehicleName());
                            etVehicleType.setText(currentVehicle.getVehicleType());
                            etVehiclePrice.setText(String.valueOf(currentVehicle.getVehiclePrice()));
                            etFuelType.setText(currentVehicle.getFuelType());
                            etEngineCapacity.setText(currentVehicle.getEngineCapacity());
                            etSeatingCapacity.setText(currentVehicle.getSeatingCapacity());
                            etColor.setText(currentVehicle.getColor());
                            etTransmission.setText(currentVehicle.getTransmission());

                            List<String> imageUrls = currentVehicle.getImageUrls();
                            if (imageUrls != null && !imageUrls.isEmpty()) {
                                Glide.with(this).load(imageUrls.get(0)).into(imgPreview); // Load the first image
                            } else if (currentVehicle.getImageUrl() != null) {
                                Glide.with(this).load(currentVehicle.getImageUrl()).into(imgPreview);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Load vehicle failed", e));
    }

    private void saveVehicle() {
        String name = etVehicleName.getText().toString().trim();
        String type = etVehicleType.getText().toString().trim();
        String priceText = etVehiclePrice.getText().toString().trim();
        String fuelType = etFuelType.getText().toString().trim();
        String engineCapacity = etEngineCapacity.getText().toString().trim();
        String seatingCapacity = etSeatingCapacity.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String transmission = etTransmission.getText().toString().trim();

        if (name.isEmpty() || type.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            uploadToCloudinaryAndSaveVehicle(name, type, priceText, fuelType, engineCapacity, seatingCapacity, color, transmission);
        } else if (vehicleId != null) {
            // If editing and no new image, save other data changes
            saveDataToFirestore(name, type, priceText, null, fuelType, engineCapacity, seatingCapacity, color, transmission);
        } else {
            Toast.makeText(this, "Please select an image for a new vehicle!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToCloudinaryAndSaveVehicle(String name, String type, String priceText, String fuelType, String engineCapacity,
                                                  String seatingCapacity, String color, String transmission) {
        Toast.makeText(this, "Uploading image...", Toast.LENGTH_LONG).show();

        MediaManager.get().upload(selectedImageUri).callback(new UploadCallback() {
            @Override
            public void onSuccess(String requestId, Map resultData) {
                String imageUrl = (String) resultData.get("secure_url");
                saveDataToFirestore(name, type, priceText, imageUrl, fuelType, engineCapacity, seatingCapacity, color, transmission);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Toast.makeText(UpdateVehicleActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
            }
            @Override public void onStart(String requestId) { }
            @Override public void onProgress(String requestId, long bytes, long totalBytes) { }
            @Override public void onReschedule(String requestId, ErrorInfo error) { }
        }).dispatch();
    }

    private void saveDataToFirestore(String name, String type, String price, String newImageUrl, String fuelType,
                                     String engineCapacity, String seatingCapacity, String color, String transmission) {

        Map<String, Object> data = new HashMap<>();
        data.put("vehicleName", name);
        data.put("vehicleType", type);
        data.put("vehiclePrice", price);
        data.put("fuelType", fuelType);
        data.put("engineCapacity", engineCapacity);
        data.put("seatingCapacity", seatingCapacity);
        data.put("color", color);
        data.put("transmission", transmission);
        data.put("providerId", currentProviderId);
        data.put("timestamp", System.currentTimeMillis());

        // --- Correctly Handle Image URLs ---
        List<String> imageUrls = new ArrayList<>();
        if (currentVehicle != null && currentVehicle.getImageUrls() != null) {
            imageUrls.addAll(currentVehicle.getImageUrls()); // Start with existing images
        }
        if (newImageUrl != null && !newImageUrl.isEmpty() && !imageUrls.contains(newImageUrl)) {
            imageUrls.add(newImageUrl); // Add the new one
        }
        data.put("imageUrls", imageUrls);

        // For backward compatibility, also update the old single imageUrl field
        if (!imageUrls.isEmpty()) {
            data.put("imageUrl", imageUrls.get(imageUrls.size() - 1));
        } else {
            data.put("imageUrl", null);
        }

        // --- Firestore Save/Update ---
        if (vehicleId != null) {
            firestore.collection("vehicles").document(vehicleId).update(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Vehicle Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Update failed", e));
        } else {
            firestore.collection("vehicles").add(data)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Vehicle Added!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Add failed", e));
        }
    }
}
