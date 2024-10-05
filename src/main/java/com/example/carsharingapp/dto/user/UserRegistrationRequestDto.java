package com.example.carsharingapp.dto.user;

import com.example.carsharingapp.enums.RoleName;
import com.example.carsharingapp.validation.EnumMatch;
import com.example.carsharingapp.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(field = "password", verifyField = "repeatPassword")
@Accessors(chain = true)
public class UserRegistrationRequestDto {
    @NotBlank(message = "Email field is mandatory")
    @Email(message = "Should be a valid email.")
    @Length(max = 255, message = "Email can not be over 255 characters")
    private String email;

    @NotBlank(message = "Password field is mandatory")
    @Length(
            min = 8,
            max = 35,
            message = "Password length can be from 8 to 35 characters"
    )
    private String password;

    @NotBlank(message = "Repeat password field is mandatory")
    @Length(
            min = 8,
            max = 35,
            message = "Password length can be from 8 to 35 characters"
    )
    private String repeatPassword;

    @NotBlank(message = "First name field is mandatory")
    @Length(
            max = 255,
            message = "First name value can not be over 255 characters"
    )
    private String firstName;

    @NotBlank(message = "Last name field is mandatory")
    @Length(max = 255, message = "Last name value can not be over 255 characters")
    private String lastName;

    @NotNull(message = "Role field is mandatory")
    @EnumMatch(enumClass = RoleName.class)
    private String role;
}
