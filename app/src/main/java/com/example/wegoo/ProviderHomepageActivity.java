package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProviderHomepageActivity extends AppCompatActivity {

    private TextView tvProviderName, tvProviderEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private static final String TAG = "ProviderHomepage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvProviderName = findViewById(R.id.tvProviderName);
        tvProviderEmail = findViewById(R.id.tvProviderEmail);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.provider_bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        loadProviderProfile();
    }

    private void loadProviderProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference docRef = mStore.collection("providers").document(uid);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("userName");
                    String email = documentSnapshot.getString("email");
                    tvProviderName.setText("Provider Name: " + name);
                    tvProviderEmail.setText("Email: " + email);
                } else {
                    Log.w(TAG, "Profile data not found.");
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to load profile: ", e);
            });
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_add_vehicle) {
            startActivity(new Intent(ProviderHomepageActivity.this, UpdateVehicleActivity.class));
            return true;
        } else if (itemId == R.id.nav_view_vehicles) {
            startActivity(new Intent(ProviderHomepageActivity.this, ProviderVehicleListActivity.class));
            return true;
        } else if (itemId == R.id.nav_history) {
            startActivity(new Intent(ProviderHomepageActivity.this, ProviderHistoryActivity.class));
            return true;
        }
        return false;
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.provider_top_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_customer_list) {
            startActivity(new Intent(ProviderHomepageActivity.this, CustomerListActivity.class));
            return true;
        } else if (itemId == R.id.action_opencv) {
            startActivity(new Intent(ProviderHomepageActivity.this, OpenCVDetectionActivity.class));
            return true;
        } else if (itemId == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProviderHomepageActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
