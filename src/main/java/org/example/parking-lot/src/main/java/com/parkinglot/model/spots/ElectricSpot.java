package com.parkinglot.model.spots;

import com.parkinglot.model.ParkingSpot;
import com.parkinglot.model.enums.ParkingSpotType;

public class ElectricSpot extends ParkingSpot {
    public ElectricSpot(String number) {
        super(number, ParkingSpotType.ELECTRIC);
    }
}
