package com.example.carsharingapp.dto.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserUpdateProfileDto {
    @Length(
            max = 255,
            message = "First name value can not be over 255 characters"
    )
    private String firstName;

    @Length(max = 255, message = "Last name value can not be over 255 characters")
    private String lastName;
}
