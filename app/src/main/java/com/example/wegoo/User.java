package com.example.wegoo;

public class User {
    private String name;
    private String phone;

    public User() {
        // Required for Firebase
    }

    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
