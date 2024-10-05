package com.example.carsharingapp.repository.payment.spec;

import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.repository.SpecificationProvider;
import com.example.carsharingapp.repository.payment.PaymentSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserPaymentSpecificationProvider implements SpecificationProvider<Payment> {
    @Override
    public String getKey() {
        return PaymentSpecificationBuilder.USER;
    }

    @Override
    public Specification<Payment> getSpecification(String param) {
        if (!isNumeric(param)) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("rental").get(PaymentSpecificationBuilder.USER).get("id"),
                Long.parseLong(param)
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
