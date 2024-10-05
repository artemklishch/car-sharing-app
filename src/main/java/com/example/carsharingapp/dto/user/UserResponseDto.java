package com.example.carsharingapp.dto.user;

import com.example.carsharingapp.enums.RoleName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private RoleName role;
}
