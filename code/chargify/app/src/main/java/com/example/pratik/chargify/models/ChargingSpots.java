package com.example.pratik.chargify.models;

public class ChargingSpots {
    double rating;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupiedSpace() {
        return occupiedSpace;
    }

    public void setOccupiedSpace(int occupiedSpace) {
        this.occupiedSpace = occupiedSpace;
    }

    public ChargingSpots(double rating, String phone, String type, String pinCode, int capacity, int occupiedSpace) {
        this.rating = rating;
        this.phone = phone;
        this.type = type;
        this.pinCode = pinCode;
        this.capacity = capacity;
        this.occupiedSpace = occupiedSpace;
    }
    public ChargingSpots()
    {

    }

    String phone;
    String type;
    String pinCode;
    int capacity;
    int occupiedSpace;
}
