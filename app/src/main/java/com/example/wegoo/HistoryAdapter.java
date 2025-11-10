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
        holder.bookingIdTextView.setText(booking.getBookingId());
        holder.carModelTextView.setText(booking.getCarModel());
        holder.dateTextView.setText(booking.getDate());
        holder.priceTextView.setText(booking.getPrice());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView bookingIdTextView;
        TextView carModelTextView;
        TextView dateTextView;
        TextView priceTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingIdTextView = itemView.findViewById(R.id.booking_id_text_view);
            carModelTextView = itemView.findViewById(R.id.car_model_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
        }
    }
}
