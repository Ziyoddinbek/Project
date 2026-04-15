package com.parkinglot.model;

import com.parkinglot.model.enums.ParkingSpotType;

public abstract class ParkingSpot {
    private String number;
    private boolean free;
    private ParkingSpotType type;
    private Vehicle vehicle;

    public ParkingSpot(String number, ParkingSpotType type) {
        this.number = number;
        this.type = type;
        this.free = true;
    }

    public boolean getIsFree() { return free; }

    public boolean assignVehicle(Vehicle vehicle) {
        if (!free) return false;
        this.vehicle = vehicle;
        this.free = false;
        return true;
    }

    public boolean removeVehicle() {
        if (free) return false;
        this.vehicle = null;
        this.free = true;
        return true;
    }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public ParkingSpotType getType() { return type; }
    public Vehicle getVehicle() { return vehicle; }
}
