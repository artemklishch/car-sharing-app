package com.example.carsharingapp.enums;

public enum PaymentStatus {
    PENDING("PENDING"),
    PAID("PAID"),
    CANCELED("CANCELED");

    private final String name;

    PaymentStatus(String paymentStatus) {
        this.name = paymentStatus;
    }
}
