package com.example.wegoo;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Vehicles");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.vehiclesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        FloatingActionButton fabAddVehicle = findViewById(R.id.addVehicleFab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(this, vehicleList, this);
        recyclerView.setAdapter(vehicleAdapter);

        db = FirebaseFirestore.getInstance();
        loadVehicles();

        fabAddVehicle.setOnClickListener(v -> {
            startActivity(new Intent(VehicleListActivity.this, ProviderHomepageActivity.class));
        });
    }

    private void loadVehicles() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("vehicles")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        vehicleList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Vehicle vehicle = document.toObject(Vehicle.class);
                            vehicle.setId(document.getId());
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
        loadVehicles();
    }

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        Intent intent = new Intent(this, UpdateVehicleActivity.class);
        intent.putExtra("vehicleId", vehicle.getId());
        startActivity(intent);
    }

    @Override
    public void onAddToCompareClick(Vehicle vehicle, boolean isChecked) {
        // Not applicable for this screen, so this is intentionally left empty.
        Toast.makeText(this, "Compare feature not available here.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookNowClick(Vehicle vehicle) {
        // Not applicable for this screen, so this is intentionally left empty.
    }
}
