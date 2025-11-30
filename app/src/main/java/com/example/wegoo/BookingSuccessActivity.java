package com.example.wegoo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookingSuccessActivity extends AppCompatActivity {

    private TextView tvVehicleName, tvVehicleType, tvVehiclePrice, tvBookingDate, tvBookingTime, tvPickupLocation;
    private Button btnOpenMaps;

    private FirebaseFirestore firestore;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_success);

        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        tvVehiclePrice = findViewById(R.id.tvVehiclePrice);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvBookingTime = findViewById(R.id.tvBookingTime);
        tvPickupLocation = findViewById(R.id.tvPickupLocation);
        btnOpenMaps = findViewById(R.id.btnOpenMaps);

        firestore = FirebaseFirestore.getInstance();

        bookingId = getIntent().getStringExtra("bookingId");
        if (bookingId != null) {
            loadBookingDetails();
        }
    }

    private void loadBookingDetails() {
        firestore.collection("bookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvVehicleName.setText(documentSnapshot.getString("vehicleName"));
                        tvVehicleType.setText(documentSnapshot.getString("vehicleType"));
                        tvVehiclePrice.setText("RM " + documentSnapshot.getDouble("vehiclePrice"));
                        tvBookingDate.setText(documentSnapshot.getString("bookingDate"));
                        tvBookingTime.setText(documentSnapshot.getString("bookingTime"));
                        tvPickupLocation.setText(documentSnapshot.getString("pickupLocation"));

                        btnOpenMaps.setOnClickListener(v -> openLocationInMaps(documentSnapshot));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load booking details", Toast.LENGTH_SHORT).show());
    }

    private void openLocationInMaps(DocumentSnapshot doc) {
        // Get location from Firestore
        String location = doc.getString("pickupLocation");

        // If Firestore is empty / null → use default address
        if (location == null || location.isEmpty()) {
            location = "NO 100 B JALAN BUKIT TEROI GUAR CHEMPEDAK 0880 GURUN KEDAH";
        }

        // Open Google Maps
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open Google Maps", Toast.LENGTH_SHORT).show();
        }
    }
}

