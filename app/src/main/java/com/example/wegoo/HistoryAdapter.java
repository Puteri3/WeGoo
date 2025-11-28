package com.example.wegoo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    public HistoryAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvVehicleName.setText(booking.getVehicleName());
        holder.tvBookingDate.setText(booking.getDate());
        holder.tvBookingPrice.setText(String.valueOf(booking.getPrice()));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvVehicleName, tvBookingDate, tvBookingPrice;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingPrice = itemView.findViewById(R.id.tvBookingPrice);
        }
    }
}
