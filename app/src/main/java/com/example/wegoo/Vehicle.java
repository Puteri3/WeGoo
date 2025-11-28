package com.example.wegoo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Vehicle implements Parcelable {
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
    private String providerId;
    private long timestamp;
    private String edition;
    private double rating;


    @Exclude
    private boolean selected;

    // Default constructor required for calls to DataSnapshot.getValue(Vehicle.class)
    public Vehicle() {
    }

    public Vehicle(String vehicleId, String vehicleName, String vehicleType, String vehiclePrice, String fuelType, String engineCapacity, String seatingCapacity, String color, ArrayList<String> imageUrls, String transmission, String providerId, long timestamp, String edition) {
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
        this.providerId = providerId;
        this.timestamp = timestamp;
        this.edition = edition;
    }

    protected Vehicle(Parcel in) {
        id = in.readString();
        vehicleName = in.readString();
        vehicleType = in.readString();
        vehiclePrice = in.readString();
        imageUrls = in.createStringArrayList();
        fuelType = in.readString();
        engineCapacity = in.readString();
        seatingCapacity = in.readString();
        color = in.readString();
        transmission = in.readString();
        isBooked = in.readByte() != 0;
        providerId = in.readString();
        timestamp = in.readLong();
        edition = in.readString();
        rating = in.readDouble();
        selected = in.readByte() != 0;
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(vehicleName);
        dest.writeString(vehicleType);
        dest.writeString(vehiclePrice);
        dest.writeStringList(imageUrls);
        dest.writeString(fuelType);
        dest.writeString(engineCapacity);
        dest.writeString(seatingCapacity);
        dest.writeString(color);
        dest.writeString(transmission);
        dest.writeByte((byte) (isBooked ? 1 : 0));
        dest.writeString(providerId);
        dest.writeLong(timestamp);
        dest.writeString(edition);
        dest.writeDouble(rating);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVehicleId(String vehicleId) {
        this.id = vehicleId;
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

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public boolean isSelected() {
        return selected;
    }

    @Exclude
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getVehicleId() {
        return id;
    }

    public String getPrice() {
        return vehiclePrice;
    }

    public double getRating() {
        return rating;
    }
}
