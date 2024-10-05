package com.example.carsharingapp.enums;

import lombok.Getter;

@Getter
public enum ModelType {
    SEDAN("SEDAN"),
    SUV("SUV"),
    HATCHBACK("HATCHBACK"),
    UNIVERSAL("UNIVERSAL");

    private final String name;

    ModelType(String roleName) {
        this.name = roleName;
    }
}
