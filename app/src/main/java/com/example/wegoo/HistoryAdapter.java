package com.example.wegoo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Booking> bookingList;

    public HistoryAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.vehicleNameTextView.setText("Vehicle: " + booking.getVehicleName());
        holder.dateTextView.setText("Date: " + booking.getDate());
        holder.timeTextView.setText("Time: " + booking.getTime());
        holder.priceTextView.setText(booking.getCurrency() + booking.getPrice());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void setBookings(List<Booking> bookings) {
        this.bookingList = bookings;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView vehicleNameTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView priceTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleNameTextView = itemView.findViewById(R.id.vehicle_name_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
        }
    }
}
