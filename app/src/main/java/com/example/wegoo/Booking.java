package com.example.wegoo;

public class Booking {

    private String bookingId;
    private String carModel;
    private String date;
    private String price;

    public Booking(String bookingId, String carModel, String date, String price) {
        this.bookingId = bookingId;
        this.carModel = carModel;
        this.date = date;
        this.price = price;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }
}
