package com.example.wegoo;

import android.os.Bundle;
import android.widget.Toast;

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

public abstract class HistoryActivity extends AppCompatActivity implements UserVehicleAdapter.OnItemClickListener {

    private UserVehicleAdapter adapter;
    private final List<Vehicle> vehicleList = new ArrayList<>();
    private DatabaseReference vehiclesRef;
    private String currentProviderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        RecyclerView rvHistoryList = findViewById(R.id.history_recycler_view);
        rvHistoryList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserVehicleAdapter(vehicleList, this);
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

                        for (DataSnapshot vehicleSnap : snapshot.getChildren()) {
                            Vehicle vehicle = vehicleSnap.getValue(Vehicle.class);
                            if (vehicle != null) {
                                vehicleList.add(vehicle);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HistoryActivity.this, "Failed to load vehicles", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBookNowClick(Vehicle vehicle) {
        Toast.makeText(this, "Book Now: " + vehicle.getVehicleName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckboxClick(Vehicle vehicle, boolean isChecked) {
        Toast.makeText(this, vehicle.getVehicleName() + " checked: " + isChecked, Toast.LENGTH_SHORT).show();
    }
}
