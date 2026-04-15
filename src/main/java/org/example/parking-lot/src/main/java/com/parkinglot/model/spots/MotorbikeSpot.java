package com.parkinglot.model.spots;

import com.parkinglot.model.ParkingSpot;
import com.parkinglot.model.enums.ParkingSpotType;

public class MotorbikeSpot extends ParkingSpot {
    public MotorbikeSpot(String number) {
        super(number, ParkingSpotType.MOTORCYCLE);
    }
}
