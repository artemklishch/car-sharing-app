package com.example.carsharingapp.dto.user;

import com.example.carsharingapp.enums.RoleName;
import com.example.carsharingapp.validation.EnumMatch;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRoleDto(
        @NotNull(message = "Role field is mandatory")
        @EnumMatch(enumClass = RoleName.class)
        String role
) {
}
