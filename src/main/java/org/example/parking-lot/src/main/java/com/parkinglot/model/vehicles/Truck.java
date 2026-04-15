package com.parkinglot.model.vehicles;

import com.parkinglot.model.Vehicle;
import com.parkinglot.model.enums.VehicleType;

public class Truck extends Vehicle {
    public Truck(String licenseNumber) {
        super(licenseNumber, VehicleType.TRUCK);
    }
}
