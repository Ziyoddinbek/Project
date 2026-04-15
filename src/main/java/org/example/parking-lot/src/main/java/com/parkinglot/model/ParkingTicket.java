package com.parkinglot.model;

import com.parkinglot.model.enums.ParkingTicketStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingTicket {
    private static final AtomicInteger COUNTER = new AtomicInteger(1000);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String ticketNumber;
    private final LocalDateTime issuedAt;
    private LocalDateTime paidAt;
    private double paidAmount;
    private ParkingTicketStatus status;
    private final Vehicle vehicle;
    private final ParkingSpot parkingSpot;
    private final String floorName;

    public ParkingTicket(Vehicle vehicle, ParkingSpot parkingSpot, String floorName) {
        this.ticketNumber = "TKT-" + COUNTER.getAndIncrement();
        this.issuedAt = LocalDateTime.now();
        this.status = ParkingTicketStatus.ACTIVE;
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.floorName = floorName;
        this.paidAmount = 0;
    }

    public String getTicketNumber() { return ticketNumber; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public String getIssuedAtFormatted() { return issuedAt.format(FMT); }
    public LocalDateTime getPaidAt() { return paidAt; }
    public String getPaidAtFormatted() { return paidAt != null ? paidAt.format(FMT) : "-"; }
    public double getPaidAmount() { return paidAmount; }
    public ParkingTicketStatus getStatus() { return status; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getParkingSpot() { return parkingSpot; }
    public String getFloorName() { return floorName; }

    public void markPaid(double amount) {
        this.paidAmount = amount;
        this.paidAt = LocalDateTime.now();
        this.status = ParkingTicketStatus.PAID;
    }

    public void markLost() {
        this.status = ParkingTicketStatus.LOST;
    }

    public boolean isActive() { return status == ParkingTicketStatus.ACTIVE; }
}
