package com.example.carsharingapp.service.impl;

import com.example.carsharingapp.dto.payment.CreatePaymentDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentSearchParamsDto;
import com.example.carsharingapp.dto.rental.PaymentResultDto;
import com.example.carsharingapp.enums.PaymentStatus;
import com.example.carsharingapp.exceptions.ProceedingException;
import com.example.carsharingapp.mapper.payment.PaymentMapper;
import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.repository.payment.PaymentRepository;
import com.example.carsharingapp.repository.payment.PaymentSpecificationBuilder;
import com.example.carsharingapp.service.PaymentService;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import com.example.carsharingapp.service.telegram.TelegramNotificationService;
import com.example.carsharingapp.utils.UrlBuilder;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final Long QUANTITY = 1L;
    private final PaymentRepository paymentRepository;
    private final PaymentSpecificationBuilder paymentSpecificationBuilder;
    private final PaymentMapper paymentMapper;
    private final TelegramNotificationService telegramNotificationService;
    private final UrlBuilder urlBuilder;

    @Value("${stripe.secret.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public List<PaymentResponseDto> getUserPaymentsByManager(
            PaymentSearchParamsDto paramsDto, Pageable pageable
    ) {
        Specification<Payment> spec = paymentSpecificationBuilder.build(paramsDto);
        return paymentRepository.findAll(spec, pageable).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentResponseDto> getUserPaymentsByCustomer(Pageable pageable, Long userId) {
        return paymentRepository.findAllByUserId(pageable, userId)
                .stream().map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponseDto createSession(CreatePaymentDto requestDto, Long userId)
            throws StripeException, MalformedURLException {
        BigDecimal amount = requestDto.getAmount();
        if (amount.compareTo(new BigDecimal(50)) < 0) {
            throw new ProceedingException("Amount must be at least 50 cents");
        }
        String query = "session_id={CHECKOUT_SESSION_ID}";
        String successUrl = urlBuilder.getBuiltUrl("/api/payments/success", query);
        String cancelUrl = urlBuilder.getBuiltUrl("/api/payments/cancel", query);
        SessionCreateParams.LineItem.PriceData.ProductData rentalData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(String.valueOf(requestDto.getRentalId()))
                        .build();
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("usd")
                        .setUnitAmount(amount.longValue())
                        .setProductData(rentalData)
                        .build();
        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(QUANTITY)
                        .setPriceData(priceData)
                        .build();
        SessionCreateParams.Builder builder = new SessionCreateParams.Builder()
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(lineItem)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .putMetadata("userId", String.valueOf(userId));
        SessionCreateParams sessionCreateParams = builder.build();
        Session session = Session.create(sessionCreateParams);
        Payment payment = paymentMapper.toEntity(requestDto);
        payment.setSessionId(session.getId());
        payment.setSession(new URL(session.getUrl()));
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentResponseDto getSuccessfulPayment(PaymentResultDto params) {
        Payment payment = paymentRepository.findBySessionId(params.getSession_id())
                .orElseThrow(() -> new ProceedingException(
                        "Payment not found with session ID: " + params.getSession_id()
                ));
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);
        telegramNotificationService.paymentNotification(paymentMapper.toDto(payment));
        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentResponseDto getCanceledPayment(PaymentResultDto params) {
        Payment payment = paymentRepository.findBySessionId(params.getSession_id())
                .orElseThrow(() -> new ProceedingException(
                        "Payment not found with session ID: " + params.getSession_id()
                ));
        payment.setStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }
}
