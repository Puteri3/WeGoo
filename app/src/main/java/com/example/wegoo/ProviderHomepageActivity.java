package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class ProviderHomepageActivity extends AppCompatActivity {

    Button btnHistory, btnCustomers, btnOpenCV, btnProfileSettings, btnUpdate, btnViewVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize buttons
        btnUpdate = findViewById(R.id.btnUpdate);
        btnHistory = findViewById(R.id.btnHistory);
        btnCustomers = findViewById(R.id.btnCustomers);
        btnOpenCV = findViewById(R.id.btnOpenCV);
        btnProfileSettings = findViewById(R.id.btnProfileSettings);
        btnViewVehicle = findViewById(R.id.btnViewVehicle);

        // Navigate to History Page
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, ProviderHistoryActivity.class);
            startActivity(intent);
        });

        // Navigate to Customer List Page
        btnCustomers.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, CustomerListActivity.class);
            startActivity(intent);
        });

        // Navigate to OpenCV Damage Detection Page
        btnOpenCV.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, OpenCVDetectionActivity.class);
            startActivity(intent);
        });

        // Navigate to Profile Settings Page
        btnProfileSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, ProviderProfileSettingsActivity.class);
            startActivity(intent);
        });

        // Navigate to Update/Manage Vehicle Page
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, UpdateVehicleActivity.class);
            startActivity(intent);
        });

        // Navigate to View Vehicle Page
        btnViewVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHomepageActivity.this, ProviderVehicleListActivity.class);
            startActivity(intent);
        });
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
            Intent intent = new Intent(ProviderHomepageActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.menu_compare) {
            // Handle compare menu item click
            return true;
        } else if (itemId == R.id.menu_about) {
            // Handle about menu item click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
