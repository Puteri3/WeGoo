package com.example.wegoo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        // Initialize RecyclerView
        rvCustomerList = findViewById(R.id.rvCustomerList);
        rvCustomerList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize customer list and adapter
        customerList = new ArrayList<>();
        customerAdapter = new CustomerAdapter(customerList);
        rvCustomerList.setAdapter(customerAdapter);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch customer data from Firestore
        fetchCustomers();

        // Set up refresh button
        Button btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(v -> {
            fetchCustomers();
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
        });

        // Set up back button
        ImageButton btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> {
            // Finish the activity to go back to the previous screen
            finish();
        });
    }

    private void fetchCustomers() {
        customerList.clear(); // Clear the list before fetching new data
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Customer customer = document.toObject(Customer.class);
                            
                            // TODO: Here you would also fetch the booking information for each customer.
                            // This would likely involve another Firestore query to a "Bookings" or "Rentals" collection,
                            // filtering by the customer's ID (document.getId()).
                            // For now, we'll just display the user info.
                            // You would then update the Customer object with vehicle and damage details.

                            customerList.add(customer);
                        }
                        customerAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        Toast.makeText(CustomerListActivity.this, "Error fetching customers.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
