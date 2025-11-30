package com.example.wegoo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProviderVehicleListActivity extends AppCompatActivity {

    private static final String TAG = "ProviderVehicleList";

    private RecyclerView recyclerView;
    private ProviderVehicleAdapter adapter;
    private List<Vehicle> vehicleList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_vehicle_list);

        recyclerView = findViewById(R.id.recycler_view); 
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Setup adapter with listeners for edit and delete
        adapter = new ProviderVehicleAdapter(this, vehicleList, new ProviderVehicleAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(Vehicle vehicle) {
                deleteVehicle(vehicle);
            }
        });
        recyclerView.setAdapter(adapter);

        loadProviderVehicles();
    }

    private void loadProviderVehicles() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String providerId = currentUser.getUid();

        db.collection("vehicles")
                .whereEqualTo("providerId", providerId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        Toast.makeText(this, "Error loading vehicles.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        vehicleList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            try {
                                Vehicle vehicle = doc.toObject(Vehicle.class);
                                vehicle.setVehicleId(doc.getId()); // Important: Set the document ID
                                vehicleList.add(vehicle);
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting document to Vehicle object", e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void deleteVehicle(Vehicle vehicle) {
        if (vehicle.getVehicleId() == null || vehicle.getVehicleId().isEmpty()) {
            Toast.makeText(this, "Error: Vehicle ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("vehicles").document(vehicle.getVehicleId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Vehicle deleted successfully.", Toast.LENGTH_SHORT).show();
                    // The snapshot listener will automatically update the list
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e);
                    Toast.makeText(this, "Failed to delete vehicle.", Toast.LENGTH_SHORT).show();
                });
    }
}
