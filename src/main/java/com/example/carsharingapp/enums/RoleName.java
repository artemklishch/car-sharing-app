package com.example.carsharingapp.enums;

import lombok.Getter;

@Getter
public enum RoleName {
    MANAGER("MANAGER"),
    CUSTOMER("CUSTOMER");

    private final String name;

    RoleName(String roleName) {
        this.name = roleName;
    }
}
