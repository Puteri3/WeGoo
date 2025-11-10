package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements VehicleAdapter.OnVehicleClickListener {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private VehicleAdapter adapter;
    private List<Vehicle> vehicleList;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("WeGoo Vehicles");
        }

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerVehicles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vehicleList = new ArrayList<>();

        // Set 'this' as the listener for the adapter
        adapter = new VehicleAdapter(this, vehicleList, this);
        recyclerView.setAdapter(adapter);

        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVehicles(s.toString());
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_booking) {
                startActivity(new Intent(this, BookingActivity.class));
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, UserHistoryActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            } else if (id == R.id.nav_features) {
                startActivity(new Intent(this, CompareTableActivity.class));
                return true;
            }
            return false;
        });

        loadVehicles();
    }

    // --- VehicleAdapter.OnVehicleClickListener Implementation ---

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        // Default action on item click: route to booking
        onBookNowClick(vehicle);
    }

    @Override
    public void onBookNowClick(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            Toast.makeText(this, "Error: Vehicle ID is missing for booking.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
        intent.putExtra("vehicleId", vehicle.getId());
        intent.putExtra("vehicleName", vehicle.getVehicleName());
        intent.putExtra("vehicleType", vehicle.getVehicleType());
        intent.putExtra("vehiclePrice", vehicle.getVehiclePrice());
        intent.putExtra("vehicleImage", vehicle.getImageUrl());
        startActivity(intent);
    }

    @Override
    public void onAddToCompareClick(Vehicle vehicle) {
        if (vehicle.getId() != null) {
            Intent intent = new Intent(HomeActivity.this, CompareTableActivity.class);
            intent.putExtra("VEHICLE_ID_TO_ADD", vehicle.getId());
            Toast.makeText(this, "Added " + vehicle.getVehicleName() + " for comparison.", Toast.LENGTH_SHORT).show();
            // Note: You might want to remove this startActivity if CompareTableActivity is a selection screen.
            // Keeping it for now to follow previous logic.
            startActivity(intent);
        } else {
            Toast.makeText(this, "Cannot add vehicle: ID is missing.", Toast.LENGTH_SHORT).show();
        }
    }


    // --- Existing Helper Methods ---

    private void loadVehicles() {
        firestore.collection("vehicles")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    vehicleList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            Vehicle v = doc.toObject(Vehicle.class);
                            // Set the Firestore document ID! CRITICAL for button actions.
                            v.setId(doc.getId());
                            vehicleList.add(v);
                        } catch (Exception e) {
                            Log.e("HomeActivity", "Error converting document: " + e.getMessage());
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("HomeActivity", "Failed to load vehicles", e));
    }

    private void filterVehicles(String text) {
        List<Vehicle> filteredList = new ArrayList<>();
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getVehicleType() != null &&
                    vehicle.getVehicleType().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(vehicle);
            }
        }
        adapter.updateList(filteredList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_compare) {
            startActivity(new Intent(this, CompareTableActivity.class));
            return true;
        } else if (id == R.id.menu_about) {
            startActivity(new Intent(this, AboutUsActivity.class));
            return true;
        } else if (id == R.id.menu_contact) {
            startActivity(new Intent(this, ContactProviderActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

