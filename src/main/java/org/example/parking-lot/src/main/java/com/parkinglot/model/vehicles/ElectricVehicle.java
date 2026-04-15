package com.parkinglot.model.vehicles;

import com.parkinglot.model.Vehicle;
import com.parkinglot.model.enums.VehicleType;

public class ElectricVehicle extends Vehicle {
    public ElectricVehicle(String licenseNumber) {
        super(licenseNumber, VehicleType.ELECTRIC);
    }
}
