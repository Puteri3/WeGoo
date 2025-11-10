package com.example.wegoo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        RecyclerView recyclerView = findViewById(R.id.history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Booking> bookingList = new ArrayList<>();
        // This is a sample booking. You can replace this with your actual booking data.
        bookingList.add(new Booking("#12345", "Myvi", "2024-07-28", "RM100"));

        HistoryAdapter adapter = new HistoryAdapter(bookingList);
        recyclerView.setAdapter(adapter);
    }
}
