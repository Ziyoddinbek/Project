package com.parkinglot.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ParkingRate {
    // $4.00 first hour, $3.50 hours 2-3, $2.50 all remaining
    private double firstHourRate = 4.00;
    private double secondThirdHourRate = 3.50;
    private double remainingHourRate = 2.50;

    public double getFirstHourRate() { return firstHourRate; }
    public void setFirstHourRate(double r) { this.firstHourRate = r; }
    public double getSecondThirdHourRate() { return secondThirdHourRate; }
    public void setSecondThirdHourRate(double r) { this.secondThirdHourRate = r; }
    public double getRemainingHourRate() { return remainingHourRate; }
    public void setRemainingHourRate(double r) { this.remainingHourRate = r; }

    /**
     * Calculates fee from issuedAt to now.
     * $4.00 first hour (minimum), $3.50 hours 2-3, $2.50 per hour after that.
     * Partial hours are rounded up.
     */
    public double calculateFee(LocalDateTime issuedAt) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(issuedAt, now);
        if (minutes < 0) minutes = 0;

        // Always charge at least 1 full hour
        long fullHours = minutes / 60;
        long remainingMinutes = minutes % 60;
        long totalHours = fullHours + (remainingMinutes > 0 ? 1 : 0);
        if (totalHours < 1) totalHours = 1;

        double fee = 0;
        for (long h = 1; h <= totalHours; h++) {
            if (h == 1) fee += firstHourRate;
            else if (h <= 3) fee += secondThirdHourRate;
            else fee += remainingHourRate;
        }
        return fee;
    }
}
