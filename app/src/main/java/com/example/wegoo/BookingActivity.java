package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private ImageView imgVehicle;
    private TextView tvVehicleName, tvVehicleType, tvVehiclePrice;
    private EditText etUserName, etUserPhone, etBookingDate, etBookingTime;
    private Button btnConfirmBooking;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Init views
        imgVehicle = findViewById(R.id.imgBookingVehicle);
        tvVehicleName = findViewById(R.id.tvBookingVehicleName);
        tvVehicleType = findViewById(R.id.tvBookingVehicleType);
        tvVehiclePrice = findViewById(R.id.tvBookingVehiclePrice);
        etUserName = findViewById(R.id.etUserName);
        etUserPhone = findViewById(R.id.etUserPhone);
        etBookingDate = findViewById(R.id.etBookingDate);
        etBookingTime = findViewById(R.id.etBookingTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        firestore = FirebaseFirestore.getInstance();

        // Get vehicle data from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("vehicleName");
        String type = intent.getStringExtra("vehicleType");
        double price = intent.getDoubleExtra("vehiclePrice", 0.0);
        String imageUrl = intent.getStringExtra("vehicleImage");

        // Set vehicle info
        tvVehicleName.setText(name);
        tvVehicleType.setText(type);
        tvVehiclePrice.setText("RM " + price);
        Glide.with(this).load(imageUrl).placeholder(R.drawable.placeholder_image).into(imgVehicle);

        // Handle booking confirmation
        btnConfirmBooking.setOnClickListener(v -> confirmBooking(name, type, price, imageUrl));
    }

    private void confirmBooking(String vehicleName, String vehicleType, double vehiclePrice, String imageUrl) {
        String userName = etUserName.getText().toString().trim();
        String userPhone = etUserPhone.getText().toString().trim();
        String bookingDate = etBookingDate.getText().toString().trim();
        String bookingTime = etBookingTime.getText().toString().trim();

        if (userName.isEmpty() || userPhone.isEmpty() || bookingDate.isEmpty() || bookingTime.isEmpty()) {
            Toast.makeText(this, "Sila isi semua maklumat.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat map untuk Firestore
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("vehicleName", vehicleName);
        bookingData.put("vehicleType", vehicleType);
        bookingData.put("vehiclePrice", vehiclePrice);
        bookingData.put("vehicleImage", imageUrl);
        bookingData.put("userName", userName);
        bookingData.put("userPhone", userPhone);
        bookingData.put("bookingDate", bookingDate);
        bookingData.put("bookingTime", bookingTime);
        bookingData.put("timestamp", System.currentTimeMillis());

        firestore.collection("bookings")
                .add(bookingData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Booking berjaya disimpan!", Toast.LENGTH_SHORT).show();
                    finish(); // kembali ke homepage
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal simpan booking. Sila cuba lagi.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
