package com.example.wegoo;

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

    public CustomerAdapter(List<Customer> customerList) {
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
        holder.bind(customerList.get(position));
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder {
        private ImageView customerProfileImageView;
        private TextView customerNameTextView;
        private TextView customerEmailTextView;
        private TextView rentedCarTextView;
        private TextView damageStatusTextView;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerProfileImageView = itemView.findViewById(R.id.customerProfileImageView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            customerEmailTextView = itemView.findViewById(R.id.customerEmailTextView);
            rentedCarTextView = itemView.findViewById(R.id.rentedCarTextView);
            damageStatusTextView = itemView.findViewById(R.id.damageStatusTextView);
        }

        void bind(Customer customer) {
            customerNameTextView.setText(customer.getUsername());
            customerEmailTextView.setText(customer.getEmail());

            // TODO: Set the rented car and damage status from a Booking/Rental object
            rentedCarTextView.setText("Rented Car: Toyota Camry");
            damageStatusTextView.setText("Damage: Scratches on the right door");
            damageStatusTextView.setVisibility(View.VISIBLE);


            // Use Glide to load the profile picture
            Glide.with(itemView.getContext())
                    .load(customer.getProfileImageUrl())
                    .placeholder(R.drawable.ic_launcher_background) // Placeholder image
                    .error(R.drawable.ic_launcher_background)       // Error image
                    .into(customerProfileImageView);
        }
    }
}
