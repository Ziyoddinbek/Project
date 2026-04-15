package com.parkinglot.model;

import com.parkinglot.model.enums.PaymentType;

import java.util.Map;

public class ExitPanel {
    private final String id;

    public ExitPanel(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public ParkingTicket scanTicket(String ticketNumber, Map<String, ParkingTicket> activeTickets) {
        return activeTickets.get(ticketNumber);
    }

    public boolean processPayment(ParkingTicket ticket, double amount, PaymentType paymentType) {
        if (ticket == null || !ticket.isActive()) return false;
        ticket.markPaid(amount);
        return true;
    }
}
