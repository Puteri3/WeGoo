package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProviderHomepageActivity extends AppCompatActivity implements VehicleAdapter.OnVehicleClickListener {

    Button btnHistory, btnCustomers, btnOpenCV, btnProfileSettings, btnUpdate;
    RecyclerView recyclerViewVehicles;
    VehicleAdapter vehicleAdapter;
    List<Vehicle> vehicleList;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_homepage);

        // Initialize buttons
        btnUpdate = findViewById(R.id.btnUpdate);
        btnHistory = findViewById(R.id.btnHistory);
        btnCustomers = findViewById(R.id.btnCustomers);
        btnOpenCV = findViewById(R.id.btnOpenCV);
        btnProfileSettings = findViewById(R.id.btnProfileSettings);

        // Initialize RecyclerView
        recyclerViewVehicles = findViewById(R.id.recyclerViewVehicles);
        recyclerViewVehicles.setHasFixedSize(true);
        recyclerViewVehicles.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(this, vehicleList, this);
        recyclerViewVehicles.setAdapter(vehicleAdapter);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("vehicles");

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                    vehicleList.add(vehicle);
                }
                vehicleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Navigate to History Page
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Navigate to Customer List Page
        btnCustomers.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, CustomerListActivity.class);
            startActivity(intent);
        });

        // Navigate to OpenCV Damage Detection Page
        btnOpenCV.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, OpenCVDetectionActivity.class);
            startActivity(intent);
        });

        // Navigate to Profile Settings Page
        btnProfileSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, ProviderProfileSettingsActivity.class);
            startActivity(intent);
        });

        // Navigate to Update/Manage Vehicle Page
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, UpdateVehicleActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        // Handle vehicle click to update
        Intent intent = new Intent(ProviderHomepageActivity.this, UpdateVehicleActivity.class);
        intent.putExtra("vehicleId", vehicle.getVehicleName()); // Assuming vehicle name is unique
        startActivity(intent);
    }

    @Override
    public void onBookNowClick(Vehicle vehicle) {
        // This action is typically for users, not providers viewing their own list.
        // If a provider needs to see booking details, the logic would go here.
        // For now, we'll keep it as a placeholder or show a toast.
        Toast.makeText(this, "Book Now feature not available on this page.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToCompareClick(Vehicle vehicle) {
        // This action is for users.
        Toast.makeText(this, "Compare feature not available on this page.", Toast.LENGTH_SHORT).show();
    }
}
