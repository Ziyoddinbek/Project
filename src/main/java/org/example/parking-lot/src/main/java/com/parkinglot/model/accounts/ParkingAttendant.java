package com.parkinglot.model.accounts;

import com.parkinglot.model.Account;
import com.parkinglot.model.ParkingTicket;
import com.parkinglot.model.Person;

public class ParkingAttendant extends Account {

    public ParkingAttendant(String userName, String password, Person person) {
        super(userName, password, person);
    }

    public boolean processTicket(ParkingTicket ticket, double amount) {
        if (ticket == null || !ticket.isActive()) return false;
        ticket.markPaid(amount);
        return true;
    }

    @Override
    public String getRole() { return "Attendant"; }
}
