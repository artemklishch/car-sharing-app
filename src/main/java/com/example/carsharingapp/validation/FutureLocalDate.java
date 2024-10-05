package com.example.carsharingapp.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = FutureLocalDateValidator.class)
@Documented
public @interface FutureLocalDate {

    String message() default "Has to be future date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
