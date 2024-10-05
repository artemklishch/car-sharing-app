package com.example.carsharingapp.repository.payment;

import com.example.carsharingapp.dto.payment.PaymentSearchParamsDto;
import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentSpecificationBuilder implements SpecificationBuilder<Payment> {
    private final SpecificationProviderManager<Payment> paymentSpecificationProviderManager;
    public static final String USER = "user";

    @Override
    public Specification<Payment> build(PaymentSearchParamsDto searchParams) {
        Specification<Payment> spec = Specification.where(null);
        String userId = searchParams.getUser_id();
        if (userId != null) {
            spec = spec.and(
                    paymentSpecificationProviderManager.getSpecificationProvider(USER)
                            .getSpecification(userId)
            );
        }
        return spec;
    }
}
