package com.example.carsharingapp.validation;

import java.util.Arrays;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<EnumMatch, String> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumMatch constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumConstant.name().equals(value));
    }
}
