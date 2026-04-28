package com.parkinglot.model;

public class EntrancePanel {
    private final String id;

    public EntrancePanel(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public ParkingTicket printTicket(Vehicle vehicle, ParkingSpot spot, String floorName) {
        ParkingTicket ticket = new ParkingTicket(vehicle, spot, floorName);
        vehicle.assignTicket(ticket);
        return ticket;
    }
}
