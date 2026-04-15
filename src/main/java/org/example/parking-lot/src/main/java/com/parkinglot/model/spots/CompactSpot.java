package com.parkinglot.model.spots;

import com.parkinglot.model.ParkingSpot;
import com.parkinglot.model.enums.ParkingSpotType;

public class CompactSpot extends ParkingSpot {
    public CompactSpot(String number) {
        super(number, ParkingSpotType.COMPACT);
    }
}
