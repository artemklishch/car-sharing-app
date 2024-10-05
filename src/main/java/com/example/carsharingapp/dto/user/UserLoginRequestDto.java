package com.example.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank(message = "Email is mandatory")
        @Length(min = 8, max = 20, message = "Email length has to be more than 8 and not over 20 characters")
        @Email(message = "Email has to be valid")
        String email,
        @NotBlank(message = "Password is mandatory")
        @Length(min = 8, max = 20, message = "Password length has to be more 8 and not over 20 characters")
        String password
) {

}
