package com.example.wegoo;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    private String id;
    private String vehicleName;
    private String vehicleType;
    private String vehiclePrice;
    private List<String> imageUrls;
    private String fuelType;
    private String engineCapacity;
    private String seatingCapacity;
    private String color;
    private String transmission;
    private boolean isBooked;


    @Exclude
    private boolean selected;

    // Default constructor required for calls to DataSnapshot.getValue(Vehicle.class)
    public Vehicle() {
    }

    public Vehicle(String vehicleId, String vehicleName, String vehicleType, String vehiclePrice, String fuelType, String engineCapacity, String seatingCapacity, String color, ArrayList<String> imageUrls, String transmission) {
        this.id = vehicleId;
        this.vehicleName = vehicleName;
        this.vehicleType = vehicleType;
        this.vehiclePrice = vehiclePrice;
        this.fuelType = fuelType;
        this.engineCapacity = engineCapacity;
        this.seatingCapacity = seatingCapacity;
        this.color = color;
        this.imageUrls = imageUrls;
        this.transmission = transmission;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehiclePrice() {
        return vehiclePrice;
    }

    public void setVehiclePrice(String vehiclePrice) {
        this.vehiclePrice = vehiclePrice;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @PropertyName("imageUrl")
    public String getImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return null;
    }

    @PropertyName("imageUrl")
    public void setImageUrl(String imageUrl) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        } else {
            this.imageUrls.clear();
        }
        this.imageUrls.add(imageUrl);
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(String engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public String getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(String seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    @Exclude
    public boolean isSelected() {
        return selected;
    }

    @Exclude
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
