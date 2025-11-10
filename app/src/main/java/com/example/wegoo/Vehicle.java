package com.example.wegoo;

public class Vehicle {
    private String id; // Critical field for database operations
    private String vehicleName;
    private String vehicleType;
    private String vehiclePrice;
    private String imageUrl; // Holds the public Firebase Storage URL
    private String fuelType;
    private String engineCapacity;
    private String seatingCapacity;
    private String color;
    private String transmission;
    private boolean isSelected = false;

    public Vehicle() {
        // Default constructor required for Firestore
    }

    public Vehicle(String vehicleName, String vehicleType, String vehiclePrice, String imageUrl, String fuelType, String engineCapacity, String seatingCapacity, String color, String transmission) {
        this.vehicleName = vehicleName;
        this.vehicleType = vehicleType;
        this.vehiclePrice = vehiclePrice;
        this.imageUrl = imageUrl;
        this.fuelType = fuelType;
        this.engineCapacity = engineCapacity;
        this.seatingCapacity = seatingCapacity;
        this.color = color;
        this.transmission = transmission;
    }

    // --- Getters and Setters ---

    // ID is crucial for Edit/Delete/Booking/Compare
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}