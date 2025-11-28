package com.example.wegoo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VehicleListActivity extends AppCompatActivity implements VehicleAdapter.OnVehicleClickListener {

    private RecyclerView recyclerView;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Vehicles");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.vehiclesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        FloatingActionButton fabAddVehicle = findViewById(R.id.addVehicleFab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(this, vehicleList, this);
        recyclerView.setAdapter(vehicleAdapter);

        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            loadVehicles();
        } else {
            // Not logged in, so can't show "My Vehicles"
            Toast.makeText(this, "You need to be logged in to view your vehicles.", Toast.LENGTH_LONG).show();
            finish(); // Close activity
            return; // Stop execution
        }

        fabAddVehicle.setOnClickListener(v -> {
            startActivity(new Intent(VehicleListActivity.this, ProviderHomepageActivity.class));
        });
    }

    private void loadVehicles() {
        progressBar.setVisibility(View.VISIBLE);
        // Filter vehicles by the current provider's ID
        db.collection("vehicles")
                .whereEqualTo("providerId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        vehicleList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Vehicle vehicle = document.toObject(Vehicle.class);
                            vehicle.setVehicleId(document.getId());
                            vehicleList.add(vehicle);
                        }
                        vehicleAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(VehicleListActivity.this, "Error loading vehicles.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload vehicles only if a user is logged in
        if (currentUserId != null) {
            loadVehicles();
        }
    }

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        // TODO: Implement this method
    }
}
