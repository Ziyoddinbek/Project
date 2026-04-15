package com.parkinglot.model.payments;

import com.parkinglot.model.Payment;
import com.parkinglot.model.enums.PaymentStatus;
import com.parkinglot.model.enums.PaymentType;

public class CashTransaction extends Payment {
    private double cashTendered;

    public CashTransaction(double amount, String ticketNumber, double cashTendered) {
        super(amount, ticketNumber, PaymentType.CASH);
        this.cashTendered = cashTendered;
    }

    @Override
    public boolean initiateTransaction() {
        if (cashTendered >= getAmount()) {
            setStatus(PaymentStatus.COMPLETED);
            return true;
        }
        setStatus(PaymentStatus.DECLINED);
        return false;
    }

    public double getCashTendered() { return cashTendered; }
    public double getChange() { return Math.max(0, cashTendered - getAmount()); }
}
