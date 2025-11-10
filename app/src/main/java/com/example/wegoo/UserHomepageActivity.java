package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserHomepageActivity extends AppCompatActivity implements UserVehicleAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private UserVehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;
    private DatabaseReference databaseReference;
    private EditText searchBar;
    private FloatingActionButton fabCompare;
    private ArrayList<Vehicle> selectedVehicles = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        recyclerView = findViewById(R.id.recyclerVehicles);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();
        vehicleAdapter = new UserVehicleAdapter(vehicleList, this);
        recyclerView.setAdapter(vehicleAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("vehicles");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                    vehicleList.add(vehicle);
                }
                vehicleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchBar = findViewById(R.id.searchBar);
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

        fabCompare = findViewById(R.id.fab_compare);
        fabCompare.setOnClickListener(v -> {
            Intent intent = new Intent(UserHomepageActivity.this, CompareTableActivity.class);
            intent.putExtra("selectedVehicles", selectedVehicles);
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
    public void onBookNowClick(int position) {
        Vehicle selectedVehicle = vehicleList.get(position);
        Intent intent = new Intent(UserHomepageActivity.this, BookingActivity.class);
        intent.putExtra("vehicleName", selectedVehicle.getVehicleName());
        intent.putExtra("vehicleType", selectedVehicle.getVehicleType());
        intent.putExtra("vehiclePrice", selectedVehicle.getVehiclePrice());
        intent.putExtra("imageUrl", selectedVehicle.getImageUrl());
        startActivity(intent);
    }

    @Override
    public void onCheckboxClick(int position, boolean isChecked) {
        vehicleList.get(position).setSelected(isChecked);
        if (isChecked) {
            selectedVehicles.add(vehicleList.get(position));
        } else {
            selectedVehicles.remove(vehicleList.get(position));
        }

        if (selectedVehicles.size() >= 2) {
            fabCompare.setVisibility(View.VISIBLE);
        } else {
            fabCompare.setVisibility(View.GONE);
        }
    }
}
