package com.parkinglot.model;

import com.parkinglot.model.enums.ParkingSpotType;
import com.parkinglot.model.spots.*;

import java.util.*;

public class ParkingFloor {
    private String name;
    private final Map<String, ParkingSpot> handicappedSpots = new LinkedHashMap<>();
    private final Map<String, ParkingSpot> compactSpots     = new LinkedHashMap<>();
    private final Map<String, ParkingSpot> largeSpots       = new LinkedHashMap<>();
    private final Map<String, ParkingSpot> motorcycleSpots  = new LinkedHashMap<>();
    private final Map<String, ParkingSpot> electricSpots    = new LinkedHashMap<>();
    private ParkingDisplayBoard displayBoard;
    private CustomerInfoPortal infoPortal;

    public ParkingFloor(String name) {
        this.name = name;
        this.displayBoard = new ParkingDisplayBoard("DB-" + name);
        this.infoPortal   = new CustomerInfoPortal("CIP-" + name);
    }

    public void addParkingSpot(ParkingSpot spot) {
        switch (spot.getType()) {
            case HANDICAPPED -> handicappedSpots.put(spot.getNumber(), spot);
            case COMPACT     -> compactSpots.put(spot.getNumber(), spot);
            case LARGE       -> largeSpots.put(spot.getNumber(), spot);
            case MOTORCYCLE  -> motorcycleSpots.put(spot.getNumber(), spot);
            case ELECTRIC    -> electricSpots.put(spot.getNumber(), spot);
        }
        updateDisplayBoard();
    }

    public boolean removeParkingSpot(String spotNumber) {
        for (Map<String, ParkingSpot> map : getAllMaps()) {
            if (map.containsKey(spotNumber)) {
                ParkingSpot spot = map.get(spotNumber);
                if (!spot.getIsFree()) return false; // occupied
                map.remove(spotNumber);
                updateDisplayBoard();
                return true;
            }
        }
        return false;
    }

    public void updateDisplayBoard() {
        Map<ParkingSpotType, Integer> counts = new HashMap<>();
        counts.put(ParkingSpotType.HANDICAPPED, (int) handicappedSpots.values().stream().filter(ParkingSpot::getIsFree).count());
        counts.put(ParkingSpotType.COMPACT,     (int) compactSpots.values().stream().filter(ParkingSpot::getIsFree).count());
        counts.put(ParkingSpotType.LARGE,       (int) largeSpots.values().stream().filter(ParkingSpot::getIsFree).count());
        counts.put(ParkingSpotType.MOTORCYCLE,  (int) motorcycleSpots.values().stream().filter(ParkingSpot::getIsFree).count());
        counts.put(ParkingSpotType.ELECTRIC,    (int) electricSpots.values().stream().filter(ParkingSpot::getIsFree).count());
        displayBoard.updateBoard(counts);
    }

    /**
     * Assigns a vehicle to the first free spot of the given type.
     * Returns the spot or null if none available.
     */
    public ParkingSpot assignVehicleToSlot(Vehicle vehicle, ParkingSpotType preferredType) {
        Map<String, ParkingSpot> map = getMapForType(preferredType);
        for (ParkingSpot spot : map.values()) {
            if (spot.getIsFree()) {
                spot.assignVehicle(vehicle);
                updateDisplayBoard();
                return spot;
            }
        }
        return null;
    }

    public boolean freeSlot(String spotNumber) {
        for (Map<String, ParkingSpot> map : getAllMaps()) {
            if (map.containsKey(spotNumber)) {
                map.get(spotNumber).removeVehicle();
                updateDisplayBoard();
                return true;
            }
        }
        return false;
    }

    public boolean isFull() {
        return getAllSpots().stream().noneMatch(ParkingSpot::getIsFree);
    }

    public int getTotalCapacity() {
        return getAllSpots().size();
    }

    public int getAvailableCount() {
        return (int) getAllSpots().stream().filter(ParkingSpot::getIsFree).count();
    }

    public int getFreeCountByType(ParkingSpotType type) {
        return (int) getMapForType(type).values().stream().filter(ParkingSpot::getIsFree).count();
    }

    public int getTotalCountByType(ParkingSpotType type) {
        return getMapForType(type).size();
    }

    public List<ParkingSpot> getAllSpots() {
        List<ParkingSpot> all = new ArrayList<>();
        for (Map<String, ParkingSpot> m : getAllMaps()) all.addAll(m.values());
        return all;
    }

    public ParkingSpot getSpotByNumber(String number) {
        for (Map<String, ParkingSpot> map : getAllMaps()) {
            if (map.containsKey(number)) return map.get(number);
        }
        return null;
    }

    private Map<String, ParkingSpot> getMapForType(ParkingSpotType type) {
        return switch (type) {
            case HANDICAPPED -> handicappedSpots;
            case COMPACT     -> compactSpots;
            case LARGE       -> largeSpots;
            case MOTORCYCLE  -> motorcycleSpots;
            case ELECTRIC    -> electricSpots;
        };
    }

    private List<Map<String, ParkingSpot>> getAllMaps() {
        return List.of(handicappedSpots, compactSpots, largeSpots, motorcycleSpots, electricSpots);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ParkingDisplayBoard getDisplayBoard() { return displayBoard; }
    public CustomerInfoPortal getInfoPortal() { return infoPortal; }
    public Map<String, ParkingSpot> getHandicappedSpots() { return Collections.unmodifiableMap(handicappedSpots); }
    public Map<String, ParkingSpot> getCompactSpots()     { return Collections.unmodifiableMap(compactSpots); }
    public Map<String, ParkingSpot> getLargeSpots()       { return Collections.unmodifiableMap(largeSpots); }
    public Map<String, ParkingSpot> getMotorcycleSpots()  { return Collections.unmodifiableMap(motorcycleSpots); }
    public Map<String, ParkingSpot> getElectricSpots()    { return Collections.unmodifiableMap(electricSpots); }
}
