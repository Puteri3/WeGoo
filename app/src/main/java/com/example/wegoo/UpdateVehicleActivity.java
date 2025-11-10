package com.example.wegoo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateVehicleActivity extends AppCompatActivity {

    private static final String TAG = "UpdateVehicleActivity";

    private FirebaseFirestore firestore;
    private StorageReference storageRef;

    private EditText etVehicleName, etVehicleType, etVehiclePrice, etFuelType, etEngineCapacity, etSeatingCapacity, etColor, etTransmission;
    private ImageView imgPreview;
    private Uri selectedImageUri;
    private String vehicleId;
    private Vehicle currentVehicle;

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
        storageRef = FirebaseStorage.getInstance().getReference("vehicle_images");

        etVehicleName = findViewById(R.id.etVehicleName);
        etVehicleType = findViewById(R.id.etVehicleType);
        etVehiclePrice = findViewById(R.id.etVehiclePrice);
        etFuelType = findViewById(R.id.etFuelType);
        etEngineCapacity = findViewById(R.id.etEngineCapacity);
        etSeatingCapacity = findViewById(R.id.etSeatingCapacity);
        etColor = findViewById(R.id.etColor);
        etTransmission = findViewById(R.id.etTransmission);
        imgPreview = findViewById(R.id.imgPreview);
        Button btnAddVehicle = findViewById(R.id.btnAddVehicle);
        Button btnUploadPicture = findViewById(R.id.btnUploadPicture);

        btnUploadPicture.setOnClickListener(v -> checkPermissionAndOpenPicker());
        btnAddVehicle.setOnClickListener(v -> saveVehicle());

        vehicleId = getIntent().getStringExtra("vehicleId");
        if (vehicleId != null) loadVehicleData();
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
                            etVehiclePrice.setText(currentVehicle.getVehiclePrice());
                            etFuelType.setText(currentVehicle.getFuelType());
                            etEngineCapacity.setText(currentVehicle.getEngineCapacity());
                            etSeatingCapacity.setText(currentVehicle.getSeatingCapacity());
                            etColor.setText(currentVehicle.getColor());
                            etTransmission.setText(currentVehicle.getTransmission());
                            String uriStr = currentVehicle.getImageUrl();
                            if (uriStr != null && !uriStr.isEmpty())
                                Glide.with(this).load(uriStr).into(imgPreview);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Load vehicle failed", e));
    }

    private void saveVehicle() {
        Log.d(TAG, "saveVehicle: Attempting to save vehicle.");
        String name = etVehicleName.getText().toString().trim();
        String type = etVehicleType.getText().toString().trim();
        String priceText = etVehiclePrice.getText().toString().trim();

        // Collect other fields
        String fuelType = etFuelType.getText().toString().trim();
        String engineCapacity = etEngineCapacity.getText().toString().trim();
        String seatingCapacity = etSeatingCapacity.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String transmission = etTransmission.getText().toString().trim();


        if (name.isEmpty() || type.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Case 1: A NEW image was selected
        if (selectedImageUri != null) {
            Log.d(TAG, "saveVehicle: New image selected. Starting upload process.");
            uploadImageAndSaveVehicle(name, type, priceText, fuelType, engineCapacity, seatingCapacity, color, transmission);
        }
        // Case 2: NO new image was selected, but we are updating an existing vehicle
        else if (vehicleId != null && currentVehicle != null && currentVehicle.getImageUrl() != null) {
            Log.d(TAG, "saveVehicle: No new image. Updating with existing image URL.");
            saveDataToFirestore(name, type, priceText, currentVehicle.getImageUrl(), fuelType, engineCapacity, seatingCapacity, color, transmission);
        }
        // Case 3: No image selected AND it's a NEW vehicle
        else {
            Log.d(TAG, "saveVehicle: No image selected for a new vehicle.");
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
        }
    }

    // ⭐ ROBUST UPLOAD FIX: Using putStream()
    private void uploadImageAndSaveVehicle(String name, String type, String priceText, String fuelType, String engineCapacity, String seatingCapacity, String color, String transmission) {
        if (selectedImageUri != null) {
            Toast.makeText(this, "Uploading image...", Toast.LENGTH_LONG).show();
            Log.d(TAG, "uploadImageAndSaveVehicle: Attempting upload from URI: " + selectedImageUri.toString());

            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(selectedImageUri));

            try {
                // Get the InputStream from the content URI
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);

                if (inputStream != null) {
                    fileReference.putStream(inputStream)
                            .addOnSuccessListener(taskSnapshot -> {
                                Log.d(TAG, "uploadImageAndSaveVehicle: Image uploaded successfully via Stream.");
                                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();

                                    // Save all data with the new public URL
                                    saveDataToFirestore(name, type, priceText, downloadUrl, fuelType, engineCapacity, seatingCapacity, color, transmission);
                                }).addOnFailureListener(e -> {
                                    Log.e(TAG, "uploadImageAndSaveVehicle: Failed to get download URL", e);
                                    Toast.makeText(UpdateVehicleActivity.this, "Failed to get image URL.", Toast.LENGTH_SHORT).show();
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e(TAG, "uploadImageAndSaveVehicle: Upload failed", e);
                            });
                } else {
                    Toast.makeText(this, "Could not open image file stream.", Toast.LENGTH_LONG).show();
                }
            } catch (FileNotFoundException e) {
                // Catch the specific error: "picture does not exist at location"
                Toast.makeText(this, "Upload failed: Picture file not accessible/found.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "uploadImageAndSaveVehicle: File Not Found Exception", e);
            }
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(cR.getType(uri));
        return extension != null ? extension : "jpg"; // Default to jpg if mime type is unknown
    }


    private void saveDataToFirestore(String name, String type, String price, String imageUrl, String fuelType, String engineCapacity, String seatingCapacity, String color, String transmission) {
        Log.d(TAG, "saveDataToFirestore: Saving data to Firestore. Image URL: " + imageUrl);
        Map<String, Object> data = new HashMap<>();
        data.put("vehicleName", name);
        data.put("vehicleType", type);
        data.put("vehiclePrice", price);
        data.put("imageUrl", imageUrl);
        data.put("fuelType", fuelType);
        data.put("engineCapacity", engineCapacity);
        data.put("seatingCapacity", seatingCapacity);
        data.put("color", color);
        data.put("transmission", transmission);

        if (vehicleId != null) {
            // Update Existing Vehicle
            firestore.collection("vehicles").document(vehicleId)
                    .update(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Vehicle Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "saveDataToFirestore: Update failed", e));
        } else {
            // Add New Vehicle
            firestore.collection("vehicles")
                    .add(data)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Vehicle Added!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "saveDataToFirestore: Add failed", e));
        }
    }
}