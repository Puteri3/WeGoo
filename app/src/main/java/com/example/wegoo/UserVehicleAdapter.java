package com.example.wegoo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UserVehicleAdapter extends RecyclerView.Adapter<UserVehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList;
    private OnItemClickListener listener;
    private List<Vehicle> selectedVehicles = new ArrayList<>();

    public interface OnItemClickListener {
        void onBookNowClick(int position);
        void onCheckboxClick(int position, boolean isChecked);
    }

    public UserVehicleAdapter(List<Vehicle> vehicleList, OnItemClickListener listener) {
        this.vehicleList = vehicleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.tvVehicleName.setText(vehicle.getVehicleName());
        holder.tvVehicleType.setText(vehicle.getVehicleType());
        holder.tvVehiclePrice.setText("RM " + vehicle.getVehiclePrice());
        Glide.with(holder.itemView.getContext()).load(vehicle.getImageUrl()).into(holder.imgVehicle);

        holder.checkboxCompare.setOnCheckedChangeListener(null);
        holder.checkboxCompare.setChecked(vehicle.isSelected());
        holder.checkboxCompare.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vehicle.setSelected(isChecked);
            if (isChecked) {
                if (!selectedVehicles.contains(vehicle)) {
                    selectedVehicles.add(vehicle);
                }
            } else {
                selectedVehicles.remove(vehicle);
            }
            if (listener != null) {
                listener.onCheckboxClick(position, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public List<Vehicle> getSelectedVehicles() {
        return selectedVehicles;
    }

    public void filterList(List<Vehicle> filteredList) {
        vehicleList = filteredList;
        notifyDataSetChanged();
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVehicle;
        TextView tvVehicleName, tvVehicleType, tvVehiclePrice;
        Button btnBookNow;
        CheckBox checkboxCompare;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVehicle = itemView.findViewById(R.id.imgVehicle);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleType);
            tvVehiclePrice = itemView.findViewById(R.id.tvVehiclePrice);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
            checkboxCompare = itemView.findViewById(R.id.checkbox_compare);

            btnBookNow.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onBookNowClick(position);
                    }
                }
            });
        }
    }
}
