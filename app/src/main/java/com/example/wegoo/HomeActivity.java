package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements VehicleAdapter.OnVehicleClickListener {

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
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

        // --- Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("WeGoo Vehicles");
        }

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // --- RecyclerView setup ---
        recyclerView = findViewById(R.id.recyclerVehicles);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        vehicleList = new ArrayList<>();
        adapter = new VehicleAdapter(this, vehicleList, this);
        recyclerView.setAdapter(adapter);

        // --- Search bar ---
        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVehicles(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // --- Pagination scroll listener ---
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

        // --- Bottom Navigation ---
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_booking) {
                startActivity(new Intent(this, BookingActivity.class));
                return true;
            } else if (id == R.id.nav_history) {
                navigateToHistory();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            }
            return false;
        });

        // --- Load vehicles initially ---
        loadVehicles();
    }

    private void navigateToHistory() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = firestore.collection("users").document(uid);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    String role = documentSnapshot.getString("role");
                    if ("provider".equals(role)) {
                        startActivity(new Intent(HomeActivity.this, ProviderHistoryActivity.class));
                    } else {
                        startActivity(new Intent(HomeActivity.this, UserHistoryActivity.class));
                    }
                } else {
                    startActivity(new Intent(HomeActivity.this, UserHistoryActivity.class));
                }
            }).addOnFailureListener(e -> {
                if (!isFinishing() && !isDestroyed()) {
                    startActivity(new Intent(HomeActivity.this, UserHistoryActivity.class));
                }
            });
        } else {
             if (!isFinishing() && !isDestroyed()) {
                startActivity(new Intent(HomeActivity.this, UserHistoryActivity.class));
            }
        }
    }

    // --- Load initial vehicles ---
    private void loadVehicles() {
        isLoading = true;
        Query firstQuery = firestore.collection("vehicles").limit(10);

        firstQuery.get().addOnSuccessListener(querySnapshot -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            if (!querySnapshot.isEmpty()) {
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Vehicle v = doc.toObject(Vehicle.class);
                    v.setVehicleId(doc.getId());
                    vehicleList.add(v);
                }
                adapter.notifyDataSetChanged();
                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
            }
            isLoading = false;
        }).addOnFailureListener(e -> isLoading = false);
    }

    private void loadMoreVehicles() {
        isLoading = true;
        Query nextQuery = firestore.collection("vehicles")
                .startAfter(lastVisible)
                .limit(10);

        nextQuery.get().addOnSuccessListener(querySnapshot -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            if (!querySnapshot.isEmpty()) {
                int startPosition = vehicleList.size();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Vehicle v = doc.toObject(Vehicle.class);
                    v.setVehicleId(doc.getId());
                    vehicleList.add(v);
                }
                adapter.notifyItemRangeInserted(startPosition, querySnapshot.size());
                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
            } else {
                lastVisible = null;
            }
            isLoading = false;
        }).addOnFailureListener(e -> isLoading = false);
    }

    // --- Filter vehicles by search query (name OR type) ---
    private void filterVehicles(String text) {
        List<Vehicle> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(vehicleList);
        } else {
            for (Vehicle vehicle : vehicleList) {
                if ((vehicle.getVehicleType() != null && vehicle.getVehicleType().toLowerCase().contains(text.toLowerCase())) ||
                        (vehicle.getVehicleName() != null && vehicle.getVehicleName().toLowerCase().contains(text.toLowerCase()))) {
                    filteredList.add(vehicle);
                }
            }
        }
        adapter.updateList(filteredList);
    }

    // --- Toolbar menu ---
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
        } else if (id == R.id.menu_contact) {
            startActivity(new Intent(this, AboutUsActivity.class));
            return true;
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        // TODO: Implement this method
    }
}
