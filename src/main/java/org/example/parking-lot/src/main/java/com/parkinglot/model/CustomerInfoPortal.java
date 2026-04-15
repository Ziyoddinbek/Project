package com.parkinglot.model;

public class CustomerInfoPortal {
    private final String id;

    public CustomerInfoPortal(String id) {
        this.id = id;
    }

    public ParkingTicket scanTicket(String ticketNumber, java.util.Map<String, ParkingTicket> activeTickets) {
        return activeTickets.get(ticketNumber);
    }

    public boolean processPayment(ParkingTicket ticket, double amount) {
        if (ticket == null || !ticket.isActive()) return false;
        ticket.markPaid(amount);
        return true;
    }

    public String getId() { return id; }
}
