package com.example.carsharingapp.enums;

public enum PaymentType {
    PAYMENT("PAYMENT"),
    FINE("FINE");

    private final String name;

    PaymentType(String paymentStatus) {
        this.name = paymentStatus;
    }
}
