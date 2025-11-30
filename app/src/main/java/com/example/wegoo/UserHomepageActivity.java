package com.example.wegoo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserHomepageActivity extends AppCompatActivity implements UserVehicleAdapter.OnItemClickListener {

    private static final String TAG = "UserHomepageActivity";
    private UserVehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;
    private final ArrayList<Vehicle> selectedVehicles = new ArrayList<>();

    private FirebaseFirestore db;

    // 🔹 NEW: TextViews for Welcome & Email
    private TextView tvWelcome, tvEmail;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // --- Initialize UI components ---
        tvWelcome = findViewById(R.id.tvWelcome);
        tvEmail = findViewById(R.id.tvEmail);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        recyclerViewSetup();
        searchBarSetup();
        fetchUserInfo();
        loadVehicles();
        bottomNavSetup();
    }

    private void bottomNavSetup() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_booking) {
                // TODO: Replace with your Booking Activity
                Toast.makeText(this, "Booking Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_history) {
                startActivity(new Intent(this, UserHistoryActivity.class));
                return true;
            }
            return false;
        });
    }

    private void recyclerViewSetup() {
        RecyclerView recyclerView = findViewById(R.id.recyclerVehicles);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();
        vehicleAdapter = new UserVehicleAdapter(vehicleList, this);
        recyclerView.setAdapter(vehicleAdapter);
    }

    private void searchBarSetup() {
        EditText searchBar = findViewById(R.id.searchBar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // 🔹 FILTER FUNCTION — cari name OR type
    private void filter(String text) {
        List<Vehicle> filteredList = new ArrayList<>();

        for (Vehicle item : vehicleList) {
            String name = item.getVehicleName() != null ? item.getVehicleName().toLowerCase() : "";
            String type = item.getVehicleType() != null ? item.getVehicleType().toLowerCase() : "";

            if (name.contains(text.toLowerCase()) || type.contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        vehicleAdapter.filterList(filteredList);
    }

    // 🔹 Load Vehicles dari Firestore
    private void loadVehicles() {
        db = FirebaseFirestore.getInstance();

        db.collection("vehicles").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "Listen failed.", error);
                return;
            }

            if (value != null) {
                vehicleList.clear();

                for (QueryDocumentSnapshot doc : value) {
                    try {
                        Vehicle vehicle = doc.toObject(Vehicle.class);
                        vehicleList.add(vehicle);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing vehicle document: " + doc.getId(), e);
                    }
                }

                vehicleAdapter.notifyDataSetChanged();
            }
        });
    }

    // 🔹 Set user info
    private void fetchUserInfo() {
        tvWelcome.setText("Welcome Leo");
        tvEmail.setText("leo@gmail.com");
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
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.menu_compare) {
            if (selectedVehicles.size() >= 2) {
                Intent intent = new Intent(this, CompareTableActivity.class);
                intent.putExtra("selectedVehicles", selectedVehicles);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Select at least two vehicles to compare.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.menu_contact) {
            startActivity(new Intent(this, AboutUsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBookNowClick(Vehicle vehicle) {
        Intent intent = new Intent(UserHomepageActivity.this, BookingActivity.class);

        intent.putExtra("vehicleName", vehicle.getVehicleName());
        intent.putExtra("vehicleType", vehicle.getVehicleType());

        try {
            double price = Double.parseDouble(vehicle.getVehiclePrice());
            intent.putExtra("vehiclePrice", price);
        } catch (Exception e) {
            intent.putExtra("vehiclePrice", 0.0);
            Log.e(TAG, "Price format invalid: " + e.getMessage());
        }

        intent.putExtra("imageUrl", vehicle.getImageUrl());
        startActivity(intent);
    }

    @Override
    public void onCheckboxClick(Vehicle vehicle, boolean isChecked) {
        vehicle.setSelected(isChecked);

        if (isChecked && !selectedVehicles.contains(vehicle)) {
            selectedVehicles.add(vehicle);
        } else if (!isChecked) {
            selectedVehicles.remove(vehicle);
        }
        Log.d(TAG, "Checkbox clicked: " + vehicle.getVehicleId() + " | " + isChecked);
    }
}
