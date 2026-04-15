package com.parkinglot.model.spots;

import com.parkinglot.model.ParkingSpot;
import com.parkinglot.model.enums.ParkingSpotType;

public class LargeSpot extends ParkingSpot {
    public LargeSpot(String number) {
        super(number, ParkingSpotType.LARGE);
    }
}
