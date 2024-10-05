package com.example.carsharingapp.repository.payment;

import com.example.carsharingapp.dto.payment.PaymentSearchParamsDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(PaymentSearchParamsDto searchParams);
}
