package com.example.carsharingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FutureLocalDateValidator implements ConstraintValidator<FutureLocalDate, String> {
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            return LocalDate.parse(value).isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
}
