package com.parkinglot.model;

import com.parkinglot.model.enums.PaymentStatus;
import com.parkinglot.model.enums.PaymentType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Payment {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime creationDate;
    private double amount;
    private PaymentStatus status;
    private final String ticketNumber;
    private final PaymentType paymentType;

    public Payment(double amount, String ticketNumber, PaymentType paymentType) {
        this.creationDate = LocalDateTime.now();
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.ticketNumber = ticketNumber;
        this.paymentType = paymentType;
    }

    public abstract boolean initiateTransaction();

    public LocalDateTime getCreationDate() { return creationDate; }
    public String getCreationDateFormatted() { return creationDate.format(FMT); }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getTicketNumber() { return ticketNumber; }
    public PaymentType getPaymentType() { return paymentType; }
}
