package com.example.wegoo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistoryList;
    private VehicleHistoryAdapter adapter;
    private List<Vehicle> vehicleList;
    private DatabaseReference vehiclesRef;
    private String currentProviderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvHistoryList = findViewById(R.id.rvHistoryList);
        rvHistoryList.setHasFixedSize(true);
        rvHistoryList.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();
        adapter = new VehicleHistoryAdapter(vehicleList);
        rvHistoryList.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentProviderId = currentUser.getUid();
        }

        vehiclesRef = FirebaseDatabase.getInstance().getReference("vehicles");
        loadProviderVehicles();
    }

    private void loadProviderVehicles() {
        vehiclesRef.orderByChild("providerId").equalTo(currentProviderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        vehicleList.clear();
                        for (DataSnapshot vehicleSnapshot : snapshot.getChildren()) {
                            Vehicle vehicle = vehicleSnapshot.getValue(Vehicle.class);
                            if (vehicle != null) {
                                vehicleList.add(vehicle);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }
}
