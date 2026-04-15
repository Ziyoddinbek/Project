package com.parkinglot.model.vehicles;

import com.parkinglot.model.Vehicle;
import com.parkinglot.model.enums.VehicleType;

public class Car extends Vehicle {
    public Car(String licenseNumber) {
        super(licenseNumber, VehicleType.CAR);
    }
}
