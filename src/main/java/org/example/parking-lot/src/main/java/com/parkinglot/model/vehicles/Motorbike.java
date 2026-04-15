package com.parkinglot.model.vehicles;

import com.parkinglot.model.Vehicle;
import com.parkinglot.model.enums.VehicleType;

public class Motorbike extends Vehicle {
    public Motorbike(String licenseNumber) {
        super(licenseNumber, VehicleType.MOTORCYCLE);
    }
}
