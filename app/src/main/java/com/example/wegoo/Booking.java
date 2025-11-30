package com.example.wegoo;

public class Booking {

    private String bookingId;
    private String vehicleName;
    private String date;
    private String time;
    private String price;
    private String currency;
    private String userId;

    // NEW FIELD
    private String pickupLocation; // default provider location

    public Booking() {
        // Required for Firebase
    }

    public Booking(String bookingId, String vehicleName, String date, String time,
                   String price, String currency, String userId, String pickupLocation) {

        this.bookingId = bookingId;
        this.vehicleName = vehicleName;
        this.date = date;
        this.time = time;
        this.price = price;
        this.currency = currency;
        this.userId = userId;
        this.pickupLocation = pickupLocation;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public String getDate() {
        return date;
    }

    public String getBookingDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Double getPrice() {
        if (price == null || price.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public Double getTotalPrice() {
        return getPrice();
    }

    public String getCurrency() {
        return currency;
    }

    public String getUserId() {
        return userId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }
}
