package com.example.carsharingapp.repository.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.utils.HandleDefaultDBRentals;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RentalRepositoryTest extends HandleDefaultDBRentals {
    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private RentalSpecificationBuilder rentalSpecificationBuilder;

    @Test
    @DisplayName("Verify find all with specification")
    void findAllWithSpecification_returnsRentals() {
        String userId = "1";
        RentalSearchParametersDto paramsDto = new RentalSearchParametersDto()
                .setUser_id(userId);
        Specification<Rental> rentalSpecification = rentalSpecificationBuilder.build(paramsDto);

        Page<Rental> actual = rentalRepository.findAll(rentalSpecification, PageRequest.of(0, 10));

        assertEquals(2, actual.getTotalElements());
        assertTrue(actual.get().findFirst().isPresent());
        assertEquals("Bob", actual.get().findFirst().get().getUser().getFirstName());
        assertEquals("BMW-600", actual.get().findFirst().get().getCar().getModel());
    }

    @Test
    @DisplayName("Verify find all by user ID")
    void findAllByUserId_returnsRentals() {
        Long userId = 1L;

        Page<Rental> actual = rentalRepository.findAllByUserId(
                userId,
                PageRequest.of(0, 10)
        );

        assertEquals(2, actual.getTotalElements());
        assertTrue(actual.get().findFirst().isPresent());
        assertEquals("Bob", actual.get().findFirst().get().getUser().getFirstName());
        assertEquals("BMW-600", actual.get().findFirst().get().getCar().getModel());
    }

    @Test
    @DisplayName("Verify fetching rental by ID and user ID")
    void getRentalByIdAndUserId_returnsRental() {
        Long userId = 1L;
        Long rentalId = 1L;
        Optional<Rental> rental = rentalRepository.findByIdAndUserId(rentalId, userId);

        assertTrue(rental.isPresent());
        assertEquals(rentalId, rental.get().getId());
    }

    @Test
    @DisplayName("Verify find by ID")
    void findById_returnsRental() {
        Long id = 1L;

        Optional<Rental> actual = rentalRepository.findById(id);

        assertTrue(actual.isPresent());
        assertEquals("BMW-600", actual.get().getCar().getModel());
    }

    @Test
    @DisplayName("Verify find all overdue")
    void findAllOverdue_returnsRentals() {
        LocalDate date = LocalDate.now().minusDays(1);

        List<Rental> actual = rentalRepository.findAllOverdue(date);

        assertEquals(2, actual.size());
    }

    @Test
    @DisplayName("Verify fetching by user id and active status query parameter")
    void getRentalsByUserIdActiveQueryParameters_returnsRentalsList() {
        String userId = "1";
        String activeStatus = "true";
        RentalSearchParametersDto paramsDto = new RentalSearchParametersDto()
                .setUser_id(userId).setIs_active(activeStatus);
        Specification<Rental> rentalSpecification = rentalSpecificationBuilder.build(paramsDto);

        Page<Rental> actual = rentalRepository.findAll(rentalSpecification, PageRequest.of(0, 10));

        assertEquals(1, actual.getTotalElements());
    }

    @Test
    @DisplayName("Verify fetching by user id and active status query parameter")
    void getRentalsByNoActiveQueryParameter_returnsRentalsList() {
        String activeStatus = "false";
        RentalSearchParametersDto paramsDto = new RentalSearchParametersDto()
                .setIs_active(activeStatus);
        Specification<Rental> rentalSpecification = rentalSpecificationBuilder.build(paramsDto);

        Page<Rental> actual = rentalRepository.findAll(rentalSpecification, PageRequest.of(0, 10));

        assertEquals(1, actual.getTotalElements());
    }
}