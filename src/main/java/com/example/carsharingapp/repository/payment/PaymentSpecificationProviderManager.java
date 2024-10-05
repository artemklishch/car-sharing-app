package com.example.carsharingapp.repository.payment;

import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.repository.SpecificationProvider;
import com.example.carsharingapp.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentSpecificationProviderManager implements SpecificationProviderManager<Payment> {
    private final List<SpecificationProvider<Payment>> specificationProviders;

    @Override
    public SpecificationProvider<Payment> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException(
                                "Can't find correct specification provider for key: " + key
                        )
                );
    }
}
