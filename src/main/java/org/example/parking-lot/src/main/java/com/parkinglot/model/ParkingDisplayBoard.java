package com.parkinglot.model;

import com.parkinglot.model.enums.ParkingSpotType;

import java.util.HashMap;
import java.util.Map;

public class ParkingDisplayBoard {
    private final String id;
    private final Map<ParkingSpotType, Integer> freeSpotCounts = new HashMap<>();

    public ParkingDisplayBoard(String id) {
        this.id = id;
        for (ParkingSpotType t : ParkingSpotType.values()) {
            freeSpotCounts.put(t, 0);
        }
    }

    public void updateBoard(Map<ParkingSpotType, Integer> counts) {
        freeSpotCounts.putAll(counts);
    }

    public int getFreeCount(ParkingSpotType type) {
        return freeSpotCounts.getOrDefault(type, 0);
    }

    public String showEmptySpotNumber() {
        StringBuilder sb = new StringBuilder("Display Board [" + id + "]: ");
        for (ParkingSpotType t : ParkingSpotType.values()) {
            sb.append(t.name()).append("=").append(freeSpotCounts.get(t)).append(" ");
        }
        return sb.toString().trim();
    }

    public String getId() { return id; }
}
