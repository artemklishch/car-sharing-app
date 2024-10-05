package com.example.carsharingapp.service;

import com.example.carsharingapp.dto.payment.CreatePaymentDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentSearchParamsDto;
import java.net.MalformedURLException;
import java.util.List;
import com.example.carsharingapp.dto.rental.PaymentResultDto;
import com.stripe.exception.StripeException;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    List<PaymentResponseDto> getUserPaymentsByManager(
            PaymentSearchParamsDto paramsDto, Pageable pageable
    );

    List<PaymentResponseDto> getUserPaymentsByCustomer(
            Pageable pageable, Long userid
    );

    PaymentResponseDto createSession(CreatePaymentDto requestDto, Long userId)
            throws StripeException, MalformedURLException;

    PaymentResponseDto getSuccessfulPayment(PaymentResultDto params);

    PaymentResponseDto getCanceledPayment(PaymentResultDto params);
}
