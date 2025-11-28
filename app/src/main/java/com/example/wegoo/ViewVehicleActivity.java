package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewVehicleActivity extends AppCompatActivity {

    private ImageView vehicleImageView;
    private TextView tvVehicleName, tvVehiclePrice, tvVehicleType, tvVehicleColor, tvFuelType, tvTransmission, tvEngineCapacity, tvSeatingCapacity;
    private Button btnEditVehicle, btnDeleteVehicle;

    private String vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);

        vehicleImageView = findViewById(R.id.vehicleImageView);
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvVehiclePrice = findViewById(R.id.tvVehiclePrice);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        tvVehicleColor = findViewById(R.id.tvVehicleColor);
        tvFuelType = findViewById(R.id.tvFuelType);
        tvTransmission = findViewById(R.id.tvTransmission);
        tvEngineCapacity = findViewById(R.id.tvEngineCapacity);
        tvSeatingCapacity = findViewById(R.id.tvSeatingCapacity);

        btnEditVehicle = findViewById(R.id.btnEditVehicle);
        btnDeleteVehicle = findViewById(R.id.btnDeleteVehicle);

        Intent intent = getIntent();
        vehicleId = intent.getStringExtra("vehicleId");

        if(vehicleId != null){
            FirebaseFirestore.getInstance().collection("vehicles").document(vehicleId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if(doc.exists()){
                            Vehicle v = doc.toObject(Vehicle.class);
                            if(v!=null){
                                tvVehicleName.setText(v.getVehicleName());
                                tvVehiclePrice.setText("RM " + v.getVehiclePrice());
                                tvVehicleType.setText(v.getVehicleType());
                                tvVehicleColor.setText(v.getColor());
                                tvFuelType.setText(v.getFuelType());
                                tvTransmission.setText(v.getTransmission());
                                tvEngineCapacity.setText(v.getEngineCapacity());
                                tvSeatingCapacity.setText(v.getSeatingCapacity());
                                Glide.with(this).load(v.getImageUrl()).into(vehicleImageView);
                            }
                        }
                    });
        }

        btnEditVehicle.setOnClickListener(v -> {
            Intent editIntent = new Intent(this, UpdateVehicleActivity.class);
            editIntent.putExtra("vehicleId", vehicleId);
            startActivity(editIntent);
        });

        btnDeleteVehicle.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure to delete this vehicle?")
                .setPositiveButton("Yes", (dialog, which) -> deleteVehicle())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteVehicle() {
        if(vehicleId==null) return;

        FirebaseFirestore.getInstance().collection("vehicles")
                .document(vehicleId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this,"Vehicle deleted",Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
