package com.example.carsharingapp.repository.rental;

import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.repository.SpecificationProvider;
import com.example.carsharingapp.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RentalSpecificationProviderManager implements SpecificationProviderManager<Rental> {
    private final List<SpecificationProvider<Rental>> specificationProviders;

    @Override
    public SpecificationProvider<Rental> getSpecificationProvider(String key) {
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
