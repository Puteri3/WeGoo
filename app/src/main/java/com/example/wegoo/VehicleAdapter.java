package com.example.wegoo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private Context context;
    private List<Vehicle> vehicleList;
    private OnVehicleClickListener onVehicleClickListener;

    public VehicleAdapter(Context context, List<Vehicle> vehicleList, OnVehicleClickListener onVehicleClickListener) {
        this.context = context;
        this.vehicleList = vehicleList;
        this.onVehicleClickListener = onVehicleClickListener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vehicle_list_item, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        holder.vehicleNameTextView.setText(vehicle.getVehicleName());
        holder.vehiclePriceTextView.setText(String.format("RM %.2f / day", vehicle.getVehiclePrice()));

        if (vehicle.getImageUrl() != null && !vehicle.getImageUrl().isEmpty()) {
            Glide.with(context).load(vehicle.getImageUrl()).into(holder.vehicleImageView);
        }

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewVehicleActivity.class);
            intent.putExtra("vehicleId", vehicle.getVehicleId());
            context.startActivity(intent);
        });

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateVehicleActivity.class);
            intent.putExtra("vehicleId", vehicle.getVehicleId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("vehicles").document(vehicle.getVehicleId()).delete();
        });

        holder.itemView.setOnClickListener(v -> {
            if(onVehicleClickListener != null) {
                onVehicleClickListener.onVehicleClick(vehicle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public void updateList(List<Vehicle> newList) {
        vehicleList = newList;
        notifyDataSetChanged();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView vehicleImageView;
        TextView vehicleNameTextView;
        TextView vehiclePriceTextView;
        Button btnView, btnEdit, btnDelete;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleImageView = itemView.findViewById(R.id.vehicleImageView);
            vehicleNameTextView = itemView.findViewById(R.id.vehicleNameTextView);
            vehiclePriceTextView = itemView.findViewById(R.id.vehiclePriceTextView);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnVehicleClickListener {
        void onVehicleClick(Vehicle vehicle);
    }
}