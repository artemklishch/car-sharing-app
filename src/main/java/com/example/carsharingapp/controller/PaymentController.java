package com.example.carsharingapp.controller;

import com.example.carsharingapp.dto.payment.CreatePaymentDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentSearchParamsDto;
import com.example.carsharingapp.dto.rental.PaymentResultDto;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.security.CustomUserDetailsService;
import com.example.carsharingapp.service.PaymentService;
import jakarta.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Get payments by user ID", description = "Get payments by user ID")
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentResponseDto> getUserPaymentsByManager(
            @Valid PaymentSearchParamsDto paramsDto,
            Pageable pageable
    ) {
        return paymentService.getUserPaymentsByManager(paramsDto, pageable);
    }

    @GetMapping("/customer")
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "Get payments by user ID", description = "Get payments by user ID")
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentResponseDto> getUserPaymentsByCustomer(
            @Valid PaymentSearchParamsDto paramsDto,
            Pageable pageable,
            Authentication authentication
    ) {
        Long userId = ((User) userDetailsService
                .loadUserByUsername(authentication.getName()))
                .getId();
        return paymentService.getUserPaymentsByCustomer(pageable, userId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "Create payment session", description = "Create payment session")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid CreatePaymentDto requestDto,
            Authentication authentication
    )
            throws StripeException, MalformedURLException {
        Long userId = ((User) userDetailsService
                .loadUserByUsername(authentication.getName()))
                .getId();
        return paymentService.createSession(requestDto, userId);
    }

    @GetMapping("/success")
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "Check successful payment", description = "Check successful payment")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponseDto getSuccessfulPayment(
            @Valid PaymentResultDto params
    ){
        return paymentService.getSuccessfulPayment(params);
    }

    @GetMapping("/cancel")
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "Check canceled payment", description = "Check canceled payment")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponseDto getCanceledPayment(
            @Valid PaymentResultDto params
    ){
        return paymentService.getCanceledPayment(params);
    }
}
