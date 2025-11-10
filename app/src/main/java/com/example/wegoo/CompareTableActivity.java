package com.example.wegoo;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CompareTableActivity extends AppCompatActivity {

    private TableLayout compareTable;
    private TableRow headerRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_table);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        compareTable = findViewById(R.id.compare_table);
        headerRow = findViewById(R.id.header_row);

        ArrayList<Vehicle> selectedVehicles = (ArrayList<Vehicle>) getIntent().getSerializableExtra("selectedVehicles");

        if (selectedVehicles != null && !selectedVehicles.isEmpty()) {
            // Add vehicle names to the header
            for (Vehicle vehicle : selectedVehicles) {
                TextView vehicleNameTextView = new TextView(this);
                vehicleNameTextView.setText(vehicle.getVehicleName());
                vehicleNameTextView.setGravity(Gravity.CENTER);
                vehicleNameTextView.setPadding(8, 8, 8, 8);
                headerRow.addView(vehicleNameTextView);
            }

            // Create a map to hold the features and their values for each vehicle
            LinkedHashMap<String, ArrayList<String>> featureMap = new LinkedHashMap<>();
            featureMap.put("Vehicle Type", new ArrayList<>());
            featureMap.put("Price", new ArrayList<>());
            featureMap.put("Fuel Type", new ArrayList<>());
            featureMap.put("Engine Capacity", new ArrayList<>());
            featureMap.put("Seating Capacity", new ArrayList<>());
            featureMap.put("Color", new ArrayList<>());
            featureMap.put("Transmission", new ArrayList<>());


            // Populate the feature map
            for (Vehicle vehicle : selectedVehicles) {
                featureMap.get("Vehicle Type").add(vehicle.getVehicleType());
                featureMap.get("Price").add(vehicle.getVehiclePrice());
                featureMap.get("Fuel Type").add(vehicle.getFuelType());
                featureMap.get("Engine Capacity").add(vehicle.getEngineCapacity());
                featureMap.get("Seating Capacity").add(vehicle.getSeatingCapacity());
                featureMap.get("Color").add(vehicle.getColor());
                featureMap.get("Transmission").add(vehicle.getTransmission());
            }

            // Add rows to the table for each feature
            for (String feature : featureMap.keySet()) {
                TableRow featureRow = new TableRow(this);
                TextView featureTextView = new TextView(this);
                featureTextView.setText(feature);
                featureTextView.setPadding(8, 8, 8, 8);
                featureRow.addView(featureTextView);

                for (String value : featureMap.get(feature)) {
                    TextView valueTextView = new TextView(this);
                    valueTextView.setText(value);
                    valueTextView.setGravity(Gravity.CENTER);
                    valueTextView.setPadding(8, 8, 8, 8);
                    featureRow.addView(valueTextView);
                }
                compareTable.addView(featureRow);
            }
        }
    }
}
