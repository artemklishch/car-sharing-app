package com.example.carsharingapp.repository.rental;

import com.example.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    private final SpecificationProviderManager<Rental> rentalSpecificationProviderManager;
    public static final String USER = "user";
    public static final String ACTUAL_RETURN_DATE = "actualReturnDate";

    @Override
    public Specification<Rental> build(RentalSearchParametersDto searchParams) {
        Specification<Rental> spec = Specification.where(null);
        String userId = searchParams.getUser_id();
        if (userId != null) {
            spec = spec.and(
                    rentalSpecificationProviderManager.getSpecificationProvider(USER)
                            .getSpecification(userId)
            );
        }
        String isActive = searchParams.getIs_active();
        boolean isActiveParamValid = isBoolean(isActive);
        if (isActiveParamValid) {
            spec = spec.and(
                    rentalSpecificationProviderManager.getSpecificationProvider(ACTUAL_RETURN_DATE)
                            .getSpecification(isActive)
            );
        }
        return spec;
    }

    private boolean isBoolean(String value) {
        return value != null &&
                (value.equalsIgnoreCase("true") ||
                        value.equalsIgnoreCase("false"));
    }
}
