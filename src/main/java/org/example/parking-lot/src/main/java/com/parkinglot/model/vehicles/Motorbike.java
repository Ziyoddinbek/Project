package com.parkinglot.model.vehicles;

import com.parkinglot.model.Vehicle;
import com.parkinglot.model.enums.VehicleType;

public class Motorbike extends Vehicle {
    public Motorbike(String licenseNumber, String brand) {
        super(licenseNumber, VehicleType.MOTORCYCLE, brand);
    }
}
