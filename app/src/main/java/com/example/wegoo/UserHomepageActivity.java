package com.example.wegoo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserHomepageActivity extends AppCompatActivity implements UserVehicleAdapter.OnItemClickListener {

    private static final String TAG = "UserHomepageActivity";
    private UserVehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;
    private FloatingActionButton fabCompare;
    private FloatingActionButton fabHistory;
    private final ArrayList<Vehicle> selectedVehicles = new ArrayList<>();

    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerVehicles);
        EditText searchBar = findViewById(R.id.searchBar);
        fabCompare = findViewById(R.id.fab_compare);
        fabHistory = findViewById(R.id.fab_history);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();
        vehicleAdapter = new UserVehicleAdapter(vehicleList, this);
        recyclerView.setAdapter(vehicleAdapter);

        db = FirebaseFirestore.getInstance();

        // 🔹 Dengar data Firestore
        db.collection("vehicles").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "Listen failed.", error);
                return;
            }

            if (value != null) {
                vehicleList.clear();

                for (QueryDocumentSnapshot doc : value) {
                    Vehicle vehicle = doc.toObject(Vehicle.class);
                    vehicleList.add(vehicle);
                }

                vehicleAdapter.notifyDataSetChanged();
            }
        });

        // 🔹 Carian kenderaan
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 🔹 Butang Compare
        fabCompare.setOnClickListener(v -> {
            Intent intent = new Intent(UserHomepageActivity.this, CompareTableActivity.class);
            intent.putExtra("selectedVehicles", selectedVehicles);
            startActivity(intent);
        });

        fabHistory.setOnClickListener(v -> {
            Intent intent = new Intent(UserHomepageActivity.this, UserHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void filter(String text) {
        List<Vehicle> filteredList = new ArrayList<>();
        for (Vehicle item : vehicleList) {
            if (item.getVehicleType().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        vehicleAdapter.filterList(filteredList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserHomepageActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.menu_compare) {
            // Handle compare menu item click
            return true;
        } else if (itemId == R.id.menu_about) {
            // Handle about menu item click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBookNowClick(int position) {
        Vehicle vehicle = vehicleList.get(position);
        Intent intent = new Intent(UserHomepageActivity.this, BookingActivity.class);
        intent.putExtra("vehicleName", vehicle.getVehicleName());
        intent.putExtra("vehicleType", vehicle.getVehicleType());
        try {
            double price = Double.parseDouble(vehicle.getVehiclePrice());
            intent.putExtra("vehiclePrice", price);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Could not parse vehicle price: " + vehicle.getVehiclePrice());
            intent.putExtra("vehiclePrice", 0.0); // Default to 0.0 if parsing fails
        }
        if (vehicle.getImageUrl() != null && !vehicle.getImageUrl().isEmpty()) {
            intent.putExtra("imageUrl", vehicle.getImageUrl());
        }
        startActivity(intent);
    }

    @Override
    public void onCheckboxClick(int position, boolean isChecked) {
        Vehicle vehicle = vehicleList.get(position);
        vehicle.setSelected(isChecked);

        if (isChecked) {
            if (!selectedVehicles.contains(vehicle)) {
                selectedVehicles.add(vehicle);
            }
        } else {
            selectedVehicles.remove(vehicle);
        }

        if (selectedVehicles.size() >= 2) {
            fabCompare.setVisibility(View.VISIBLE);
        } else {
            fabCompare.setVisibility(View.GONE);
        }
    }
}
