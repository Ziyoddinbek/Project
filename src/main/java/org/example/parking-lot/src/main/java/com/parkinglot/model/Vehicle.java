package com.parkinglot.model;

import com.parkinglot.model.enums.VehicleType;

public abstract class Vehicle {
    private String licenseNumber;
    private VehicleType type;
    private String brand;
    private ParkingTicket ticket;

    public Vehicle(String licenseNumber, VehicleType type, String brand) {
        this.licenseNumber = licenseNumber;
        this.type = type;
        this.brand = brand;
    }

    public void assignTicket(ParkingTicket ticket) {
        this.ticket = ticket;
    }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public VehicleType getType() { return type; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public ParkingTicket getTicket() { return ticket; }
}
