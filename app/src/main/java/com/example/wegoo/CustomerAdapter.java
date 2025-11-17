package com.example.wegoo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customerList;
    private Context context;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        holder.tvName.setText(customer.getName());
        holder.tvEmail.setText(customer.getEmail());
        holder.tvVehicleName.setText("Vehicle: " + customer.getVehicleName());
        holder.tvVehicleType.setText("Type: " + customer.getVehicleType());
        holder.tvVehiclePrice.setText("Price: RM" + customer.getVehiclePrice());
        holder.tvBookingDate.setText("Date: " + customer.getBookingDate());
        holder.tvBookingTime.setText("Time: " + customer.getBookingTime());
        holder.tvPickupLocation.setText("Pickup: " + customer.getPickupLocation());
        holder.tvUserPhone.setText("Phone: " + customer.getUserPhone());

        Glide.with(context).load(customer.getImageUrl()).into(holder.ivVehicle);
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvVehicleName, tvVehicleType, tvVehiclePrice,
                tvBookingDate, tvBookingTime, tvPickupLocation, tvUserPhone;
        ImageView ivVehicle;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvEmail = itemView.findViewById(R.id.tvCustomerEmail);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleType);
            tvVehiclePrice = itemView.findViewById(R.id.tvVehiclePrice);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            tvPickupLocation = itemView.findViewById(R.id.tvPickupLocation);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            ivVehicle = itemView.findViewById(R.id.ivVehicle);
        }
    }
}
