package com.example.wegoo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private Context context;
    private List<Vehicle> vehicleList;
    private OnVehicleClickListener listener;

    public interface OnVehicleClickListener {
        void onVehicleClick(Vehicle vehicle);
        void onBookNowClick(Vehicle vehicle);
        void onAddToCompareClick(Vehicle vehicle);
        // Note: If you are using this adapter for a provider list that has Edit/Delete,
        // you need to add onEditClick and onDeleteClick here as well.
    }

    public VehicleAdapter(Context context, List<Vehicle> vehicleList, OnVehicleClickListener listener) {
        this.context = context;
        this.vehicleList = vehicleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        holder.tvVehicleName.setText(vehicle.getVehicleName());
        holder.tvVehicleType.setText(vehicle.getVehicleType());
        holder.tvVehiclePrice.setText(vehicle.getVehiclePrice());
        Glide.with(holder.itemView.getContext()).load(vehicle.getImageUrl()).into(holder.imgVehicle);

        // Listener for the entire item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVehicleClick(vehicle);
            }
        });

        // Listener for "Book Now" Button
        holder.btnBookNow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookNowClick(vehicle);
            }
        });

        // Listener for "Add to Compare" Button
        holder.btnAddCompare.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCompareClick(vehicle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public void updateList(List<Vehicle> newList) {
        this.vehicleList = newList;
        notifyDataSetChanged();
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVehicle;
        TextView tvVehicleName, tvVehicleType, tvVehiclePrice;
        Button btnBookNow;
        Button btnAddCompare;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVehicle = itemView.findViewById(R.id.imgVehicle);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleType);
            tvVehiclePrice = itemView.findViewById(R.id.tvVehiclePrice);

            // Initialize buttons based on the new layout
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
            btnAddCompare = itemView.findViewById(R.id.btnAddCompare);
        }
    }
}