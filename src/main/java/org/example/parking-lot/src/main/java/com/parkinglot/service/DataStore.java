package com.parkinglot.service;

import com.parkinglot.model.*;
import com.parkinglot.model.accounts.Admin;
import com.parkinglot.model.accounts.ParkingAttendant;
import com.parkinglot.model.enums.ParkingSpotType;
import com.parkinglot.model.enums.VehicleType;
import com.parkinglot.model.spots.*;
import com.parkinglot.model.vehicles.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton that holds all in-memory application state.
 */
public class DataStore {

    private static DataStore instance;

    private ParkingLot parkingLot;
    private final List<Account> accounts = new ArrayList<>();
    private final List<Payment> paymentHistory = new ArrayList<>();
    private Account loggedInUser = null;

    private DataStore() {
        initSampleData();
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // ─── Sample Data ──────────────────────────────────────────────────────────

    private void initSampleData() {
        Location address = new Location("123 Main St", "Central City", "CA", "90001", "USA");
        parkingLot = new ParkingLot("PL-001", "Central City Parking", address);

        // Entrance / Exit panels
        parkingLot.addEntrancePanel(new EntrancePanel("ENT-1"));
        parkingLot.addEntrancePanel(new EntrancePanel("ENT-2"));
        parkingLot.addExitPanel(new ExitPanel("EXT-1"));
        parkingLot.addExitPanel(new ExitPanel("EXT-2"));

        // ── Floor 1 ──────────────────────────────────────────────────────────
        ParkingFloor f1 = new ParkingFloor("Floor-1");
        addSpots(f1, ParkingSpotType.COMPACT,     "F1-C", 5);
        addSpots(f1, ParkingSpotType.LARGE,        "F1-L", 3);
        addSpots(f1, ParkingSpotType.HANDICAPPED,  "F1-H", 2);
        addSpots(f1, ParkingSpotType.MOTORCYCLE,   "F1-M", 2);
        addSpots(f1, ParkingSpotType.ELECTRIC,     "F1-E", 2);
        parkingLot.addParkingFloor(f1);

        // ── Floor 2 ──────────────────────────────────────────────────────────
        ParkingFloor f2 = new ParkingFloor("Floor-2");
        addSpots(f2, ParkingSpotType.COMPACT,     "F2-C", 6);
        addSpots(f2, ParkingSpotType.LARGE,        "F2-L", 4);
        addSpots(f2, ParkingSpotType.HANDICAPPED,  "F2-H", 2);
        addSpots(f2, ParkingSpotType.MOTORCYCLE,   "F2-M", 2);
        addSpots(f2, ParkingSpotType.ELECTRIC,     "F2-E", 1);
        parkingLot.addParkingFloor(f2);

        // ── Floor 3 ──────────────────────────────────────────────────────────
        ParkingFloor f3 = new ParkingFloor("Floor-3");
        addSpots(f3, ParkingSpotType.COMPACT,    "F3-C", 8);
        addSpots(f3, ParkingSpotType.LARGE,       "F3-L", 4);
        addSpots(f3, ParkingSpotType.HANDICAPPED, "F3-H", 1);
        addSpots(f3, ParkingSpotType.MOTORCYCLE,  "F3-M", 3);
        parkingLot.addParkingFloor(f3);

        // ── Accounts ─────────────────────────────────────────────────────────
        Location adminAddr = new Location("1 Admin Ave", "Central City", "CA", "90001", "USA");
        Person adminPerson = new Person("Alice Admin", adminAddr, "alice@parking.com", "555-0001");
        accounts.add(new Admin("admin", "admin123", adminPerson));

        Location attAddr = new Location("2 Attendant Blvd", "Central City", "CA", "90001", "USA");
        Person attPerson = new Person("Bob Attendant", attAddr, "bob@parking.com", "555-0002");
        accounts.add(new ParkingAttendant("attendant", "att123", attPerson));

        // ── Pre-park 4 vehicles ───────────────────────────────────────────────
        prePark("ABC-1234", VehicleType.CAR,        ParkingSpotType.COMPACT);
        prePark("XYZ-5678", VehicleType.TRUCK,       ParkingSpotType.LARGE);
        prePark("EV-0001",  VehicleType.ELECTRIC,    ParkingSpotType.ELECTRIC);
        prePark("MOTO-99",  VehicleType.MOTORCYCLE,  ParkingSpotType.MOTORCYCLE);
    }

    private void addSpots(ParkingFloor floor, ParkingSpotType type, String prefix, int count) {
        for (int i = 1; i <= count; i++) {
            String number = prefix + i;
            ParkingSpot spot = switch (type) {
                case COMPACT     -> new CompactSpot(number);
                case LARGE       -> new LargeSpot(number);
                case HANDICAPPED -> new HandicappedSpot(number);
                case MOTORCYCLE  -> new MotorbikeSpot(number);
                case ELECTRIC    -> new ElectricSpot(number);
            };
            floor.addParkingSpot(spot);
        }
    }

    private void prePark(String plate, VehicleType vType, ParkingSpotType sType) {
        Vehicle v = createVehicle(plate, vType);
        ParkingTicket ticket = parkingLot.getNewParkingTicket(v, sType);
        // Adjust issued time to simulate vehicles already parked for a while
        // (ticket is already in activeTickets via getNewParkingTicket)
    }

    public Vehicle createVehicle(String plate, VehicleType type) {
        return switch (type) {
            case CAR        -> new Car(plate);
            case TRUCK      -> new Truck(plate);
            case ELECTRIC   -> new ElectricVehicle(plate);
            case VAN        -> new Van(plate);
            case MOTORCYCLE -> new Motorbike(plate);
        };
    }

    // ─── Accessors ────────────────────────────────────────────────────────────

    public ParkingLot getParkingLot() { return parkingLot; }
    public List<Account> getAccounts() { return accounts; }
    public List<Payment> getPaymentHistory() { return paymentHistory; }
    public void addPayment(Payment p) { paymentHistory.add(p); }

    public Account getLoggedInUser() { return loggedInUser; }
    public void setLoggedInUser(Account user) { this.loggedInUser = user; }

    public Account authenticate(String username, String password) {
        return accounts.stream()
                .filter(a -> a.authenticate(username, password))
                .findFirst()
                .orElse(null);
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public boolean removeAccount(String username) {
        return accounts.removeIf(a -> a.getUserName().equals(username)
                && !(a instanceof Admin)); // can't remove admins
    }
}
