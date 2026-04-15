package com.parkinglot.model;

import com.parkinglot.model.enums.ParkingSpotType;
import com.parkinglot.model.enums.VehicleType;

import java.util.*;

public class ParkingLot {
    private String id;
    private String name;
    private Location address;
    private final List<ParkingFloor> floors = new ArrayList<>();
    private final List<EntrancePanel> entrancePanels = new ArrayList<>();
    private final List<ExitPanel> exitPanels = new ArrayList<>();
    private ParkingRate parkingRate;
    private final Map<String, ParkingTicket> activeTickets = new LinkedHashMap<>();

    public ParkingLot(String id, String name, Location address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.parkingRate = new ParkingRate();
    }

    public void addParkingFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public boolean removeParkingFloor(String floorName) {
        return floors.removeIf(f -> f.getName().equals(floorName) && f.getAvailableCount() == f.getTotalCapacity());
    }

    public void addEntrancePanel(EntrancePanel panel) {
        entrancePanels.add(panel);
    }

    public void addExitPanel(ExitPanel panel) {
        exitPanels.add(panel);
    }

    /**
     * Issues a new parking ticket for the given vehicle.
     * Tries to find a spot matching the preferred type; falls back to handicapped if needed.
     * Returns null if lot is full.
     */
    public ParkingTicket getNewParkingTicket(Vehicle vehicle, ParkingSpotType preferredType) {
        if (isFull()) return null;

        // Electric vehicles must use electric spots
        if (vehicle.getType() == VehicleType.ELECTRIC) {
            for (ParkingFloor floor : floors) {
                ParkingSpot spot = floor.assignVehicleToSlot(vehicle, ParkingSpotType.ELECTRIC);
                if (spot != null) {
                    ParkingTicket ticket = new ParkingTicket(vehicle, spot, floor.getName());
                    vehicle.assignTicket(ticket);
                    activeTickets.put(ticket.getTicketNumber(), ticket);
                    return ticket;
                }
            }
            return null; // No electric spots available
        }

        // Try preferred type first
        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.assignVehicleToSlot(vehicle, preferredType);
            if (spot != null) {
                ParkingTicket ticket = new ParkingTicket(vehicle, spot, floor.getName());
                vehicle.assignTicket(ticket);
                activeTickets.put(ticket.getTicketNumber(), ticket);
                return ticket;
            }
        }

        // Fallback: try other non-electric, non-handicapped types
        List<ParkingSpotType> fallbacks = new ArrayList<>(List.of(
                ParkingSpotType.COMPACT, ParkingSpotType.LARGE,
                ParkingSpotType.MOTORCYCLE, ParkingSpotType.HANDICAPPED));
        fallbacks.remove(preferredType);

        for (ParkingSpotType fallbackType : fallbacks) {
            if (fallbackType == ParkingSpotType.ELECTRIC) continue;
            for (ParkingFloor floor : floors) {
                ParkingSpot spot = floor.assignVehicleToSlot(vehicle, fallbackType);
                if (spot != null) {
                    ParkingTicket ticket = new ParkingTicket(vehicle, spot, floor.getName());
                    vehicle.assignTicket(ticket);
                    activeTickets.put(ticket.getTicketNumber(), ticket);
                    return ticket;
                }
            }
        }
        return null;
    }

    public boolean isFull() {
        return floors.stream().allMatch(ParkingFloor::isFull);
    }

    public int getTotalCapacity() {
        return floors.stream().mapToInt(ParkingFloor::getTotalCapacity).sum();
    }

    public int getAvailableSpots() {
        return floors.stream().mapToInt(ParkingFloor::getAvailableCount).sum();
    }

    public double getTodayRevenue() {
        return activeTickets.values().stream()
                .filter(t -> !t.isActive())
                .mapToDouble(ParkingTicket::getPaidAmount)
                .sum();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Location getAddress() { return address; }
    public void setAddress(Location address) { this.address = address; }
    public List<ParkingFloor> getFloors() { return Collections.unmodifiableList(floors); }
    public List<EntrancePanel> getEntrancePanels() { return Collections.unmodifiableList(entrancePanels); }
    public List<ExitPanel> getExitPanels() { return Collections.unmodifiableList(exitPanels); }
    public ParkingRate getParkingRate() { return parkingRate; }
    public void setParkingRate(ParkingRate parkingRate) { this.parkingRate = parkingRate; }
    public Map<String, ParkingTicket> getActiveTickets() { return Collections.unmodifiableMap(activeTickets); }

    public void removeFromActiveTickets(String ticketNumber) {
        activeTickets.remove(ticketNumber);
    }
}
