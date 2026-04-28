package com.parkinglot.model.payments;

import com.parkinglot.model.Payment;
import com.parkinglot.model.enums.PaymentStatus;
import com.parkinglot.model.enums.PaymentType;

public class CreditCardTransaction extends Payment {
    private String nameOnCard;

    public CreditCardTransaction(double amount, String ticketNumber, String nameOnCard) {
        super(amount, ticketNumber, PaymentType.CREDIT_CARD);
        this.nameOnCard = nameOnCard;
    }

    @Override
    public boolean initiateTransaction() {
        setStatus(PaymentStatus.COMPLETED);
        return true;
    }

    public String getNameOnCard() { return nameOnCard; }
}
