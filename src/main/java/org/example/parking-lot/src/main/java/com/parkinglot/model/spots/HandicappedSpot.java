package com.parkinglot.model.spots;

import com.parkinglot.model.ParkingSpot;
import com.parkinglot.model.enums.ParkingSpotType;

public class HandicappedSpot extends ParkingSpot {
    public HandicappedSpot(String number) {
        super(number, ParkingSpotType.HANDICAPPED);
    }
}
