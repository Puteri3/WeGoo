package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAllVehiclesActivity extends AppCompatActivity {

    private RecyclerView rvVehicles;
    private VehicleAdapter adapter;
    private List<Vehicle> vehicleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_vehicles);

        rvVehicles = findViewById(R.id.rvVehicles);
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();

        // ✅ Tambah listener kosong / buka detail bila click
        adapter = new VehicleAdapter(this, vehicleList, vehicle -> {
            // buka detail vehicle
            Intent intent = new Intent(ViewAllVehiclesActivity.this, ViewVehicleActivity.class);
            intent.putExtra("vehicleId", vehicle.getVehicleId());
            startActivity(intent);
        });

        rvVehicles.setAdapter(adapter);

        loadVehicles();
    }

    private void loadVehicles() {
        FirebaseFirestore.getInstance().collection("vehicles")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    vehicleList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Vehicle v = doc.toObject(Vehicle.class);
                        v.setVehicleId(doc.getId()); // set Firestore doc ID
                        vehicleList.add(v);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load vehicles", Toast.LENGTH_SHORT).show();
                    Log.e("ViewAllVehicles", "Load error", e);
                });
    }
}
