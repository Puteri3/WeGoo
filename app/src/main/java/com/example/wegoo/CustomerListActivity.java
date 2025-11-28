package com.example.wegoo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CustomerListActivity extends AppCompatActivity {

    private static final String TAG = "CustomerListActivity";

    private RecyclerView rvCustomerList;
    private CustomerAdapter customerAdapter;
    private List<Customer> customerList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        rvCustomerList = findViewById(R.id.rvCustomerList);
        rvCustomerList.setLayoutManager(new LinearLayoutManager(this));

        customerList = new ArrayList<>();
        customerAdapter = new CustomerAdapter(customerList);
        rvCustomerList.setAdapter(customerAdapter);

        db = FirebaseFirestore.getInstance();

        fetchCustomers();

        Button btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(v -> {
            fetchCustomers();
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
        });

        ImageButton btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> finish());
    }

    private void fetchCustomers() {
        customerList.clear();

        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot userDoc : task.getResult()) {
                            Customer customer = userDoc.toObject(Customer.class);

                            if (customer == null) continue;

                            String userName = customer.getUserName(); // Guna userName, bukan name

                            // Fetch booking terbaru berdasarkan userName
                            db.collection("bookings")
                                    .whereEqualTo("userName", userName)
                                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnCompleteListener(bookingTask -> {
                                        if (bookingTask.isSuccessful() && !bookingTask.getResult().isEmpty()) {
                                            DocumentSnapshot bookingDoc = bookingTask.getResult().getDocuments().get(0);
                                            customer.setVehicleName(bookingDoc.getString("vehicleName"));
                                            customer.setVehiclePrice(bookingDoc.getDouble("vehiclePrice"));
                                            customer.setVehicleType(bookingDoc.getString("vehicleType"));
                                        } else {
                                            customer.setVehicleName("No booking");
                                            customer.setVehiclePrice(0.0);
                                            customer.setVehicleType("-");
                                        }

                                        customerList.add(customer);
                                        customerAdapter.notifyDataSetChanged();
                                    });
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        Toast.makeText(CustomerListActivity.this, "Error fetching customers.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
