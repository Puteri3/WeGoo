package com.example.wegoo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
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

        final Intent intent = getIntent();
        final String name = intent.getStringExtra("vehicleName");
        final String type = intent.getStringExtra("vehicleType");
        final String imageUrl = intent.getStringExtra("imageUrl");

        double priceValue = 0.0;
        if (intent.hasExtra("vehiclePrice")) {
            Object priceExtra = intent.getExtras().get("vehiclePrice");
            if (priceExtra instanceof Double) {
                priceValue = (Double) priceExtra;
            } else if (priceExtra instanceof String) {
                try {
                    priceValue = Double.parseDouble((String) priceExtra);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Invalid vehicle price format", Toast.LENGTH_SHORT).show();
                }
            }
        }
        
        final double finalPrice = priceValue;

        tvVehicleName.setText(name);
        tvVehicleType.setText(type);
        tvVehiclePrice.setText("RM " + String.format(Locale.getDefault(), "%.2f", finalPrice));
        Glide.with(this).load(imageUrl).placeholder(R.drawable.placeholder_image).into(imgVehicle);

        etBookingDate.setOnClickListener(v -> showDatePickerDialog());
        etBookingTime.setOnClickListener(v -> showTimePickerDialog());

        btnConfirmBooking.setOnClickListener(v -> confirmBooking(name, type, finalPrice, imageUrl));
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    etBookingDate.setText(selectedDate);
                }, year, month, day);

        // ✅ Set minimum date ke hari ini supaya user tak boleh pilih tarikh lepas
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

        datePickerDialog.show();
    }


    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    etBookingTime.setText(selectedTime);
                }, hour, minute, false);
        timePickerDialog.show();
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

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("vehicleName", vehicleName);
        bookingData.put("vehicleType", vehicleType);
        bookingData.put("vehiclePrice", vehiclePrice);
        bookingData.put("imageUrl", imageUrl);
        bookingData.put("userName", userName);
        bookingData.put("userPhone", userPhone);
        bookingData.put("bookingDate", bookingDate);
        bookingData.put("bookingTime", bookingTime);
        bookingData.put("timestamp", System.currentTimeMillis());

        // Set pickupLocation kosong dahulu — nanti PaymentActivity update
        bookingData.put("pickupLocation", "");

        firestore.collection("bookings")
                .add(bookingData)
                .addOnSuccessListener(documentReference -> {

                    String bookingId = documentReference.getId(); // ❤️ DAPAT BOOKING ID FIRESTORE

                    Toast.makeText(this, "Booking berjaya disimpan!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
                    intent.putExtra("vehiclePrice", vehiclePrice);
                    intent.putExtra("bookingId", bookingId); // ❤️ HANTAR BOOKING ID
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal simpan booking. Sila cuba lagi.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
