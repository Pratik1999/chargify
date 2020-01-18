package com.example.pratik.chargify.models;

/**
 * Created by pratik on 11/1/19.
 */

public class ParkingSpot {
    ChargingSpots cs;
    SpotLocation sp;

    public ParkingSpot(ChargingSpots cs, SpotLocation sp) {
        this.cs = cs;
        this.sp = sp;
    }

    public ChargingSpots getCs() {
        return cs;
    }

    public void setCs(ChargingSpots cs) {
        this.cs = cs;
    }

    public SpotLocation getSp() {
        return sp;
    }

    public void setSp(SpotLocation sp) {
        this.sp = sp;
    }

    public ParkingSpot(double latitude, double longitude, double rating, String phone, String type, String pinCode, int capacity, int occupiedSpace) {
        this.sp=new SpotLocation(latitude,longitude);
        this.cs=new ChargingSpots(rating,phone,type,pinCode,capacity,occupiedSpace);
    }
    public double getLatitude() {
        return sp.latitude;
    }

    public double getLongitude() {
        return sp.longitude;
    }

    public double getRating() {
        return cs.rating;
    }

    public String getPhone() {
        return cs.phone;
    }

    public String getType() {
        return cs.type;
    }

    public String getPinCode() {
        return cs.pinCode;
    }

    public int getCapacity() {
        return cs.capacity;
    }

    public int getOccupiedSpace() {
        return cs.occupiedSpace;
    }

  }
