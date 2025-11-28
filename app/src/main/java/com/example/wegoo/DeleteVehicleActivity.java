
package com.example.wegoo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class DeleteVehicleActivity extends AppCompatActivity {

    private TextView tvVehicleName, tvVehicleType, tvVehiclePrice;
    private ImageView imgPreview;
    private Button btnDelete, btnClose;

    private FirebaseFirestore firestore;
    private String vehicleId;
    private ListenerRegistration vehicleListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_vehicle);

        // Initialize UI
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        tvVehiclePrice = findViewById(R.id.tvVehiclePrice);
        imgPreview = findViewById(R.id.imgPreview);
        btnDelete = findViewById(R.id.btnDelete);
        btnClose = findViewById(R.id.btnClose);

        firestore = FirebaseFirestore.getInstance();
        vehicleId = getIntent().getStringExtra("vehicleId");

        startVehicleListener();

        btnClose.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void startVehicleListener() {
        DocumentReference vehicleRef = firestore.collection("vehicles").document(vehicleId);
        vehicleListener = vehicleRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Toast.makeText(this, "Failed to load vehicle", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                tvVehicleName.setText(snapshot.getString("vehicleName"));
                tvVehicleType.setText(snapshot.getString("vehicleType"));
                tvVehiclePrice.setText(snapshot.getString("vehiclePrice"));

                String imageUrl = snapshot.getString("imageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this).load(imageUrl).into(imgPreview);
                }
            } else {
                Toast.makeText(this, "Vehicle not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Vehicle")
                .setMessage("Are you sure you want to delete this vehicle?")
                .setPositiveButton("Yes", (dialog, which) -> deleteVehicle())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteVehicle() {
        firestore.collection("vehicles")
                .document(vehicleId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Vehicle deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete vehicle", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vehicleListener != null) vehicleListener.remove();
    }
}

