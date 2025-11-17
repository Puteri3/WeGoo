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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements VehicleAdapter.OnVehicleClickListener {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private VehicleAdapter adapter;
    private List<Vehicle> vehicleList;
    private EditText searchBar;
    private ArrayList<Vehicle> selectedForCompareList = new ArrayList<>();

    // For pagination
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        vehicleList = new ArrayList<>();

        adapter = new VehicleAdapter(this, vehicleList, this);
        recyclerView.setAdapter(adapter);

        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVehicles(s.toString());
            }
        });

        // Add scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() == vehicleList.size() - 1) {
                    if (lastVisible != null) {
                        loadMoreVehicles();
                    }
                }
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
            }
            return false;
        });

        loadVehicles();
    }

    // --- VehicleAdapter.OnVehicleClickListener Implementation ---
    @Override
    public void onVehicleClick(Vehicle vehicle) {
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
        if (vehicle.getImageUrl() != null && !vehicle.getImageUrl().isEmpty()) {
             String firstImageUrl = vehicle.getImageUrl();
             intent.putExtra("imageUrl", firstImageUrl);
             Log.d("Problem", "Image URL: " + firstImageUrl);
        }
        startActivity(intent);
    }

    @Override
    public void onAddToCompareClick(Vehicle vehicle, boolean isChecked) {
        if (isChecked) {
            if (!selectedForCompareList.contains(vehicle)) {
                selectedForCompareList.add(vehicle);
                Toast.makeText(this, vehicle.getVehicleName() + " added to comparison.", Toast.LENGTH_SHORT).show();
            }
        } else {
            selectedForCompareList.remove(vehicle);
            Toast.makeText(this, vehicle.getVehicleName() + " removed from comparison.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Modified Helper Methods for Pagination ---

    private void loadVehicles() {
        isLoading = true;
        Query firstQuery = firestore.collection("vehicles").limit(10);

        firstQuery.get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    try {
                        Vehicle v = doc.toObject(Vehicle.class);
                        v.setId(doc.getId());
                        vehicleList.add(v);
                    } catch (Exception e) {
                        Log.e("HomeActivity", "Error converting document: " + e.getMessage());
                    }
                }
                adapter.notifyDataSetChanged();
                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
            }
            isLoading = false;
        }).addOnFailureListener(e -> {
            Log.e("HomeActivity", "Failed to load vehicles", e);
            isLoading = false;
        });
    }

    private void loadMoreVehicles() {
        isLoading = true;
        Query nextQuery = firestore.collection("vehicles")
                .startAfter(lastVisible)
                .limit(10);

        nextQuery.get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                int startPosition = vehicleList.size();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    try {
                        Vehicle v = doc.toObject(Vehicle.class);
                        v.setId(doc.getId());
                        vehicleList.add(v);
                    } catch (Exception e) {
                        Log.e("HomeActivity", "Error converting document: " + e.getMessage());
                    }
                }
                adapter.notifyItemRangeInserted(startPosition, querySnapshot.size());
                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
            } else {
                lastVisible = null;
            }
            isLoading = false;
        }).addOnFailureListener(e -> {
            Log.e("HomeActivity", "Failed to load more vehicles", e);
            isLoading = false;
        });
    }

    private void filterVehicles(String text) {
        // Note: This now only filters the currently loaded vehicles.
        // For a full database search, this implementation would need to change.
        List<Vehicle> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            // If search is cleared, ideally we should re-load the paginated list.
            // For now, we'll just show what we have.
             filteredList.addAll(vehicleList);
        } else {
            for (Vehicle vehicle : vehicleList) {
                if (vehicle.getVehicleType() != null &&
                        vehicle.getVehicleType().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(vehicle);
                }
            }
        }
        adapter.updateList(filteredList);
    }
    
    // --- Unchanged Options Menu Methods ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_compare) {
            if (selectedForCompareList.size() > 1) {
                Intent intent = new Intent(this, CompareTableActivity.class);
                intent.putExtra("selectedVehicles", selectedForCompareList);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select at least two vehicles to compare.", Toast.LENGTH_SHORT).show();
            }
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
