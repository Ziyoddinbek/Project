package com.parkinglot.model.vehicles;

import com.parkinglot.model.Vehicle;
import com.parkinglot.model.enums.VehicleType;

public class Truck extends Vehicle {
    public Truck(String licenseNumber, String brand) {
        super(licenseNumber, VehicleType.TRUCK, brand);
    }
}
