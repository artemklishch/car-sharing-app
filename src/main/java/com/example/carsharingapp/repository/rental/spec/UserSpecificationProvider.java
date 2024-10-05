package com.example.carsharingapp.repository.rental.spec;

import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.repository.SpecificationProvider;
import com.example.carsharingapp.repository.rental.RentalSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecificationProvider implements SpecificationProvider<Rental> {
    @Override
    public String getKey() {
        return RentalSpecificationBuilder.USER;
    }

    @Override
    public Specification<Rental> getSpecification(String param) {
        if (!isNumeric(param)) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(RentalSpecificationBuilder.USER).get("id"), Long.parseLong(param)
        );
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
