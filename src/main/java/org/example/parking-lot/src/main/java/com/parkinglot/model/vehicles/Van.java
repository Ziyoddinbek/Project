package com.parkinglot.model.vehicles;

import com.parkinglot.model.Vehicle;
import com.parkinglot.model.enums.VehicleType;

public class Van extends Vehicle {
    public Van(String licenseNumber) {
        super(licenseNumber, VehicleType.VAN);
    }
}
