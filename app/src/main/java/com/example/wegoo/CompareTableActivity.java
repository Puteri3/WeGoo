package com.example.wegoo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CompareTableActivity extends AppCompatActivity {

    private Spinner spinnerVehicle1, spinnerVehicle2;
    private Button btnCompare;
    private LinearLayout comparisonLayout;
    private TextView tvVehicle1Name, tvVehicle2Name, tvVehicle1Price, tvVehicle2Price, tvVehicle1Rating, tvVehicle2Rating, tvVehicle1Edition, tvVehicle2Edition;

    private FirebaseFirestore db;
    private List<Vehicle> vehicleList;
    private List<String> vehicleNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_table);

        spinnerVehicle1 = findViewById(R.id.spinner_vehicle1);
        spinnerVehicle2 = findViewById(R.id.spinner_vehicle2);
        btnCompare = findViewById(R.id.btn_compare);
        comparisonLayout = findViewById(R.id.comparison_layout);
        tvVehicle1Name = findViewById(R.id.tv_vehicle1_name);
        tvVehicle2Name = findViewById(R.id.tv_vehicle2_name);
        tvVehicle1Price = findViewById(R.id.tv_vehicle1_price);
        tvVehicle2Price = findViewById(R.id.tv_vehicle2_price);
        tvVehicle1Rating = findViewById(R.id.tv_vehicle1_rating);
        tvVehicle2Rating = findViewById(R.id.tv_vehicle2_rating);
        tvVehicle1Edition = findViewById(R.id.tv_vehicle1_edition);
        tvVehicle2Edition = findViewById(R.id.tv_vehicle2_edition);

        db = FirebaseFirestore.getInstance();
        vehicleList = new ArrayList<>();
        vehicleNameList = new ArrayList<>();

        loadVehicles();

        if (btnCompare != null) {
            btnCompare.setOnClickListener(v -> compareVehicles());
        } else {
            Log.e("CompareTableActivity", "Compare button not found in the layout.");
            Toast.makeText(this, "UI Error: Compare button is missing.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadVehicles() {
        db.collection("vehicles").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        Vehicle vehicle = document.toObject(Vehicle.class);
                        vehicleList.add(vehicle);
                        if (vehicle.getVehicleName() != null) {
                            vehicleNameList.add(vehicle.getVehicleName());
                        }
                    } catch (Throwable t) {
                        Log.e("CompareTableActivity", "Error converting vehicle document", t);
                    }
                }
                if (spinnerVehicle1 != null && spinnerVehicle2 != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehicleNameList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerVehicle1.setAdapter(adapter);
                    spinnerVehicle2.setAdapter(adapter);
                } else {
                    Log.e("CompareTableActivity", "Spinner views are not found in the layout.");
                    Toast.makeText(CompareTableActivity.this, "UI Error: Could not find vehicle spinners.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(CompareTableActivity.this, "Error loading vehicles.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void compareVehicles() {
        if (vehicleList.isEmpty()) {
            Toast.makeText(this, "No vehicles available to compare.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerVehicle1 == null || spinnerVehicle2 == null) {
            Toast.makeText(this, "UI Error: Spinners not initialized.", Toast.LENGTH_SHORT).show();
            return;
        }

        int pos1 = spinnerVehicle1.getSelectedItemPosition();
        int pos2 = spinnerVehicle2.getSelectedItemPosition();

        if (pos1 == -1 || pos2 == -1) {
            Toast.makeText(this, "Please select vehicles.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pos1 == pos2) {
            Toast.makeText(this, "Please select two different vehicles.", Toast.LENGTH_SHORT).show();
            return;
        }

        Vehicle vehicle1 = vehicleList.get(pos1);
        Vehicle vehicle2 = vehicleList.get(pos2);

        if (tvVehicle1Name == null || tvVehicle2Name == null || tvVehicle1Price == null || tvVehicle2Price == null ||
            tvVehicle1Rating == null || tvVehicle2Rating == null || tvVehicle1Edition == null || tvVehicle2Edition == null) {
            Toast.makeText(this, "UI Error: TextViews for comparison are missing.", Toast.LENGTH_LONG).show();
            Log.e("CompareTableActivity", "One or more TextViews for comparison are null.");
            return;
        }

        tvVehicle1Name.setText(vehicle1.getVehicleName());
        tvVehicle2Name.setText(vehicle2.getVehicleName());

        tvVehicle1Price.setText(String.valueOf(vehicle1.getVehiclePrice()));
        tvVehicle2Price.setText(String.valueOf(vehicle2.getVehiclePrice()));

        tvVehicle1Rating.setText(String.valueOf(vehicle1.getRating()));
        tvVehicle2Rating.setText(String.valueOf(vehicle2.getRating()));
        
        tvVehicle1Edition.setText(vehicle1.getEdition() != null ? vehicle1.getEdition() : "N/A");
        tvVehicle2Edition.setText(vehicle2.getEdition() != null ? vehicle2.getEdition() : "N/A");

        if (comparisonLayout != null) {
            comparisonLayout.setVisibility(View.VISIBLE);
        }
    }
}
