package com.example.wegoo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private Context context;
    private List<Vehicle> vehicleList;
    private OnVehicleClickListener listener;

    // Listener interface
    public interface OnVehicleClickListener {
        void onVehicleClick(Vehicle vehicle);
        void onBookNowClick(Vehicle vehicle);
        void onAddToCompareClick(Vehicle vehicle, boolean isChecked);
    }

    public VehicleAdapter(Context context, List<Vehicle> vehicleList, OnVehicleClickListener listener) {
        this.context = context;
        this.vehicleList = vehicleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        // Set text fields
        holder.tvVehicleName.setText(vehicle.getVehicleName());
        holder.tvVehicleType.setText(vehicle.getVehicleType());
        holder.tvVehiclePrice.setText(vehicle.getVehiclePrice());

        // Horizontal image list inside each vehicle
        if (vehicle.getImageUrl() != null && !vehicle.getImageUrl().isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            imageUrls.add(vehicle.getImageUrl());

            ImageSliderAdapter adapter = new ImageSliderAdapter(
                    holder.itemView.getContext(),
                    imageUrls
            );

            holder.viewPager.setAdapter(adapter);
        }

        // Full item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVehicleClick(vehicle);
            }
        });

        // Book Now
        holder.btnBookNow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookNowClick(vehicle);
            }
        });

        // Add to Compare
        holder.checkCompare.setOnCheckedChangeListener(null);
        holder.checkCompare.setChecked(vehicle.isSelected());
        holder.checkCompare.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vehicle.setSelected(isChecked);
            if (listener != null) {
                listener.onAddToCompareClick(vehicle, isChecked);
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

        TextView tvVehicleName, tvVehicleType, tvVehiclePrice;
        Button btnBookNow;
        CheckBox checkCompare;
        ViewPager2 viewPager;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleType);
            tvVehiclePrice = itemView.findViewById(R.id.tvVehiclePrice);

            btnBookNow = itemView.findViewById(R.id.btnBookNow);
            checkCompare = itemView.findViewById(R.id.checkCompare);

            viewPager = itemView.findViewById(R.id.viewPager);
        }
    }
}
