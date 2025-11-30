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
        holder.vehicleNameTextView.setText(booking.getVehicleName());
        holder.bookingDateTextView.setText(booking.getBookingDate());
        holder.totalPriceTextView.setText(String.valueOf(booking.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView vehicleNameTextView;
        TextView bookingDateTextView;
        TextView totalPriceTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleNameTextView = itemView.findViewById(R.id.vehicle_name_text_view);
            bookingDateTextView = itemView.findViewById(R.id.booking_date_text_view);
            totalPriceTextView = itemView.findViewById(R.id.total_price_text_view);
        }
    }
}
