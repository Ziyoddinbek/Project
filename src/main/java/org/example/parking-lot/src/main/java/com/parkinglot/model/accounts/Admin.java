package com.parkinglot.model.accounts;

import com.parkinglot.model.Account;
import com.parkinglot.model.ParkingFloor;
import com.parkinglot.model.ParkingLot;
import com.parkinglot.model.Person;

public class Admin extends Account {

    public Admin(String userName, String password, Person person) {
        super(userName, password, person);
    }

    public boolean addParkingFloor(ParkingLot lot, ParkingFloor floor) {
        lot.addParkingFloor(floor);
        return true;
    }

    @Override
    public String getRole() { return "Admin"; }
}
