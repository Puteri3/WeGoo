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
import java.util.List;

public class ProviderVehicleAdapter extends RecyclerView.Adapter<ProviderVehicleAdapter.ViewHolder> {

    private final Context context;
    private final List<Vehicle> vehicleList;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Vehicle vehicle);
    }

    public ProviderVehicleAdapter(Context context, List<Vehicle> vehicleList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.vehicleList = vehicleList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_provider_vehicle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        holder.tvVehicleName.setText(vehicle.getVehicleName());
        Glide.with(context).load(vehicle.getImageUrl()).into(holder.imgVehicle);

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewVehicleActivity.class);
            intent.putExtra("vehicleId", vehicle.getVehicleId());
            context.startActivity(intent);
        });

        holder.btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateVehicleActivity.class);
            intent.putExtra("vehicleId", vehicle.getVehicleId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(vehicle));
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVehicleName;
        ImageView imgVehicle;
        Button btnView, btnUpdate, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            imgVehicle = itemView.findViewById(R.id.imgVehicle);
            btnView = itemView.findViewById(R.id.btnView);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
