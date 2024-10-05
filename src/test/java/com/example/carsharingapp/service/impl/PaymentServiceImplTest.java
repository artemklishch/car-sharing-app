package com.example.carsharingapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

import com.example.carsharingapp.dto.payment.CreatePaymentDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentSearchParamsDto;
import com.example.carsharingapp.dto.rental.PaymentResultDto;
import com.example.carsharingapp.enums.PaymentStatus;
import com.example.carsharingapp.enums.PaymentType;
import com.example.carsharingapp.exceptions.ProceedingException;
import com.example.carsharingapp.mapper.payment.PaymentMapper;
import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.repository.payment.PaymentRepository;
import com.example.carsharingapp.repository.payment.PaymentSpecificationBuilder;
import com.example.carsharingapp.service.telegram.TelegramNotificationService;
import com.example.carsharingapp.utils.UrlBuilder;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    private static Payment TEST_PAYMENT;

    private static PaymentResponseDto TEST_PAYMENT_RESPONSE_DTO;

    private static CreatePaymentDto TEST_CREATE_PAYMENT_DTO;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TelegramNotificationService telegramNotificationService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentSpecificationBuilder paymentSpecificationBuilder;

    @Mock
    private UrlBuilder urlBuilder;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeAll
    static void beforeAll() throws MalformedURLException {
        TEST_CREATE_PAYMENT_DTO = new CreatePaymentDto()
                .setAmount(BigDecimal.valueOf(1200))
                .setPaymentType(String.valueOf(PaymentType.PAYMENT))
                .setRentalId(1L);
        TEST_PAYMENT = new Payment()
                .setId(1L)
                .setStatus(PaymentStatus.PENDING)
                .setType(PaymentType.valueOf(TEST_CREATE_PAYMENT_DTO.getPaymentType()))
                .setRental(new Rental().setId(TEST_CREATE_PAYMENT_DTO.getRentalId()))
                .setSession(new URL("http://some-session-url"))
                .setSessionId("some-session-id")
                .setAmount(TEST_CREATE_PAYMENT_DTO.getAmount());
        TEST_PAYMENT_RESPONSE_DTO = new PaymentResponseDto()
                .setId(TEST_PAYMENT.getId())
                .setAmount(TEST_PAYMENT.getAmount())
                .setStatus(TEST_PAYMENT.getStatus())
                .setType(TEST_PAYMENT.getType())
                .setRental(TEST_PAYMENT.getRental().getId())
                .setSessionId(TEST_PAYMENT.getSessionId());
    }

    @Test
    @DisplayName("Verify getUserPayments")
    void getUserPayments_returnsPaymentResponseDtos() {
        Pageable pageable = Pageable.unpaged();
        PaymentSearchParamsDto paramsDto = new PaymentSearchParamsDto()
                .setUser_id("1");
        Specification<Payment> mockSpec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        when(paymentSpecificationBuilder.build(paramsDto)).thenReturn(mockSpec);
        when(paymentRepository.findAll(mockSpec, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(TEST_PAYMENT)));
        when(paymentMapper.toDto(TEST_PAYMENT)).thenReturn(TEST_PAYMENT_RESPONSE_DTO);

        List<PaymentResponseDto> result = paymentService.getUserPaymentsByManager(paramsDto, pageable);

        assertEquals(1, result.size());
        assertEquals(TEST_PAYMENT_RESPONSE_DTO, result.get(0));
    }

    @Test
    @DisplayName("Verify error when creating session ans amount is less the 50 cents")
    void createSession_less50CentsPayment_throwsException() {
        Long userId = 1L;
        CreatePaymentDto requestDto = new CreatePaymentDto()
                .setAmount(BigDecimal.valueOf(10))
                .setPaymentType(String.valueOf(PaymentType.PAYMENT))
                .setRentalId(1L);

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> paymentService.createSession(requestDto, userId)
        );

        assertEquals("Amount must be at least 50 cents", exception.getMessage());
    }

    @Test
    @DisplayName("Successfully creates Stripe session when amount is valid")
    void createSession_validAmount_createsStripeSession()
            throws StripeException, MalformedURLException {
        Long userId = 1L;

        Session session = Mockito.mock(Session.class);
        when(session.getId()).thenReturn(TEST_PAYMENT.getSessionId());
        when(session.getUrl()).thenReturn("http://example.com/checkout-session");

        mockStatic(Session.class);
        when(Session.create(any(SessionCreateParams.class))).thenReturn(session);

        when(paymentMapper.toEntity(any(CreatePaymentDto.class))).thenReturn(TEST_PAYMENT);
        when(paymentRepository.save(any(Payment.class))).thenReturn(TEST_PAYMENT);
        when(paymentMapper.toDto(TEST_PAYMENT)).thenReturn(TEST_PAYMENT_RESPONSE_DTO);

        PaymentResponseDto dto = paymentService.createSession(TEST_CREATE_PAYMENT_DTO, userId);

        assertEquals(TEST_PAYMENT.getSessionId(), dto.getSessionId());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Verify payment success when payment not found")
    void getSuccessfulPayment_whenPaymentNotFound_throwsException() {
        PaymentResultDto resultDto = new PaymentResultDto()
                .setSession_id("some-session-id");
        when(paymentRepository.findBySessionId(resultDto.getSession_id()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> paymentService.getSuccessfulPayment(resultDto)
        );

        assertEquals(
                "Payment not found with session ID: " + resultDto.getSession_id(),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Verify payment success")
    void getSuccessfulPayment_returnsPaymentResponseDto() {
        PaymentResultDto resultDto = new PaymentResultDto()
                .setSession_id("some-session-id");
        when(paymentRepository.findBySessionId(resultDto.getSession_id()))
                .thenReturn(Optional.of(TEST_PAYMENT));
        TEST_PAYMENT.setStatus(PaymentStatus.PAID);
        TEST_PAYMENT_RESPONSE_DTO.setStatus(PaymentStatus.PAID);
        when(paymentRepository.save(any(Payment.class))).thenReturn(TEST_PAYMENT);
        when(paymentMapper.toDto(TEST_PAYMENT)).thenReturn(TEST_PAYMENT_RESPONSE_DTO);

        PaymentResponseDto result = paymentService.getSuccessfulPayment(resultDto);

        assertEquals(TEST_PAYMENT_RESPONSE_DTO, result);
        assertEquals(TEST_PAYMENT_RESPONSE_DTO.getStatus(), PaymentStatus.PAID);
    }

    @Test
    @DisplayName("Verify payment cancel when payment not found")
    void getCanceledPayment_whenPaymentNotFound_throwsException() {
        PaymentResultDto resultDto = new PaymentResultDto()
                .setSession_id("some-session-id");
        when(paymentRepository.findBySessionId(resultDto.getSession_id()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> paymentService.getSuccessfulPayment(resultDto)
        );

        assertEquals(
                "Payment not found with session ID: " + resultDto.getSession_id(),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Verify payment canceled")
    void getCanceledPayment_returnsPaymentResponseDto() {
        PaymentResultDto resultDto = new PaymentResultDto()
                .setSession_id("some-session-id");
        when(paymentRepository.findBySessionId(resultDto.getSession_id()))
                .thenReturn(Optional.of(TEST_PAYMENT));
        TEST_PAYMENT.setStatus(PaymentStatus.CANCELED);
        TEST_PAYMENT_RESPONSE_DTO.setStatus(PaymentStatus.CANCELED);
        when(paymentRepository.save(any(Payment.class))).thenReturn(TEST_PAYMENT);
        when(paymentMapper.toDto(TEST_PAYMENT)).thenReturn(TEST_PAYMENT_RESPONSE_DTO);

        PaymentResponseDto result = paymentService.getSuccessfulPayment(resultDto);

        assertEquals(TEST_PAYMENT_RESPONSE_DTO, result);
        assertEquals(TEST_PAYMENT_RESPONSE_DTO.getStatus(), PaymentStatus.CANCELED);
    }
}