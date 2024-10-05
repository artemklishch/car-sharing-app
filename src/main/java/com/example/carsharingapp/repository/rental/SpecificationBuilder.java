package com.example.carsharingapp.repository.rental;

import com.example.carsharingapp.dto.rental.RentalSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(RentalSearchParametersDto searchParams);
}
