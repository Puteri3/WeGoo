package com.example.wegoo;

public class Customer {
    private String name;       // dari Users collection
    private String userName;   // dari Users collection
    private String email;      // dari Users collection

    private String vehicleName;
    private String vehicleType;
    private Double vehiclePrice;

    private String bookingDate;
    private String bookingTime;
    private String userPhone;
    private String imageUrl;
    private String pickupLocation;

    public Customer() {} // Firestore

    // Constructor penuh
    public Customer(String name, String userName, String email,
                    String vehicleName, String vehicleType, Double vehiclePrice,
                    String bookingDate, String bookingTime, String userPhone,
                    String imageUrl, String pickupLocation) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.vehicleName = vehicleName;
        this.vehicleType = vehicleType;
        this.vehiclePrice = vehiclePrice;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.userPhone = userPhone;
        this.imageUrl = imageUrl;
        this.pickupLocation = pickupLocation;
    }

    // Getter & Setter semua field
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Double getVehiclePrice() { return vehiclePrice; }
    public void setVehiclePrice(Double vehiclePrice) { this.vehiclePrice = vehiclePrice; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getBookingTime() { return bookingTime; }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
}
