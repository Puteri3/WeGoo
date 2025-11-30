package com.example.wegoo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProviderHistoryAdapter extends RecyclerView.Adapter<ProviderHistoryAdapter.ProviderHistoryViewHolder> {

    private Context context;
    private List<Vehicle> vehicleList;

    public ProviderHistoryAdapter(Context context, List<Vehicle> vehicleList) {
        this.context = context;
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public ProviderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ProviderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderHistoryViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.tvVehicleName.setText(vehicle.getVehicleName());
        holder.tvBookingDate.setText(""); // No date available in Vehicle model
        holder.tvBookingPrice.setText(String.valueOf(vehicle.getVehiclePrice()));
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    class ProviderHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvVehicleName, tvBookingDate, tvBookingPrice;

        public ProviderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingPrice = itemView.findViewById(R.id.tvBookingPrice);
        }
    }
}
