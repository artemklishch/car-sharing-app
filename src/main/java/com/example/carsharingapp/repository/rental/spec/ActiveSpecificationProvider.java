package com.example.carsharingapp.repository.rental.spec;

import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.repository.SpecificationProvider;
import com.example.carsharingapp.repository.rental.RentalSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ActiveSpecificationProvider implements SpecificationProvider<Rental> {
    @Override
    public String getKey() {
        return RentalSpecificationBuilder.ACTUAL_RETURN_DATE;
    }

    @Override
    public Specification<Rental> getSpecification(String param) {
        if (param.equalsIgnoreCase("true")) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(
                    root.get(RentalSpecificationBuilder.ACTUAL_RETURN_DATE)
            );
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(
                root.get(RentalSpecificationBuilder.ACTUAL_RETURN_DATE)
        );
    }

}
