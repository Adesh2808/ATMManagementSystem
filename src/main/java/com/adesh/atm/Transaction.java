package com.adesh.atm;

import java.time.LocalDateTime;

public record Transaction(LocalDateTime dateTime, String type, double amount, double balanceAfter) {
    @Override
    public String toString() {
        return dateTime + " | " + type + " | ₹" + amount + " | Balance: ₹" + balanceAfter;
    }
}
