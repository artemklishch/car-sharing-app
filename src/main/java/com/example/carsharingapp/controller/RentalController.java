package com.example.carsharingapp.controller;

import com.example.carsharingapp.dto.rental.AddRentalDto;
import com.example.carsharingapp.dto.rental.AddedRentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.security.CustomUserDetailsService;
import com.example.carsharingapp.service.RentalService;
import com.example.carsharingapp.service.telegram.TelegramNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;
    private final CustomUserDetailsService userDetailsService;
    private final TelegramNotificationService telegramNotificationService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get rental by ID", description = "Get rental by ID")
    @ResponseStatus(HttpStatus.OK)
    public RentalResponseDto getRentalById(@PathVariable Long id) {
        return rentalService.getRentalByIdByManager(id);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/customer/{id}")
    @Operation(summary = "Get rental by ID", description = "Get rental by ID")
    @ResponseStatus(HttpStatus.OK)
    public RentalResponseDto getRentalByIdByCustomer(
            @PathVariable Long id, Authentication authentication
    ) {
        User user = (User) userDetailsService
                .loadUserByUsername(authentication.getName());
        return rentalService.getRentalByIdByCustomer(id, user);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping
    @Operation(summary = "Add rental", description = "Add rental")
    @ResponseStatus(HttpStatus.CREATED)
    public AddedRentalResponseDto addRental(
            @RequestBody @Valid AddRentalDto requestDto,
            Authentication authentication
    ) {
        User user = (User) userDetailsService
                .loadUserByUsername(authentication.getName());
        Map<String, Object> resultData = rentalService.addRental(requestDto, user);
        telegramNotificationService.addRentalNotification(
                (Rental) resultData.get("rental"), (Car) resultData.get("car")
        );
        return (AddedRentalResponseDto) resultData.get("responseDto");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    @Operation(
            summary = "Get rentals by params by manager",
            description = "Get rentals by query parameters by manager: user ID, is active status"
    )
    @ResponseStatus(HttpStatus.OK)
    public List<RentalResponseDto> searchRentalByParams(
            RentalSearchParametersDto params,
            Pageable pageable
    ) {
        return rentalService.searchRentalsByManager(params, pageable);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/customer")
    @Operation(
            summary = "Get rentals by customer",
            description = "Get rentals by customer"
    )
    @ResponseStatus(HttpStatus.OK)
    public List<RentalResponseDto> searchRentalByCustomer(
            RentalSearchParametersDto params,
            Pageable pageable,
            Authentication authentication
    ) {
        User user = (User) userDetailsService
                .loadUserByUsername(authentication.getName());
        return rentalService.searchRentalsByCustomer(params, pageable, user);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/{id}/return")
    @Operation(summary = "Set rental returned", description = "Set rental returned")
    @ResponseStatus(HttpStatus.OK)
    public void setRentalReturned(
            @PathVariable Long id
    ) {
        Map<String, Object> resultData = rentalService.returnRental(id);
        telegramNotificationService.addRentalNotification(
                (Rental) resultData.get("rental"), (Car) resultData.get("car")
        );
    }
}
