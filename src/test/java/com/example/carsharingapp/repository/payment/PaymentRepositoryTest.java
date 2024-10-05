package com.example.carsharingapp.repository.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.carsharingapp.dto.payment.PaymentSearchParamsDto;
import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.utils.HandleDefaultDBPayments;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest extends HandleDefaultDBPayments {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentSpecificationBuilder paymentSpecificationBuilder;

    @Test
    @DisplayName("Verify fetching payments by user ID")
    void getPaymentsByUserId_returnsPaymentsListInPage() {
        String userId = "1";
        PaymentSearchParamsDto paramsDto = new PaymentSearchParamsDto()
                .setUser_id(userId);
        Specification<Payment> paymentSpecification = paymentSpecificationBuilder.build(paramsDto);

        Page<Payment> paymentsPage = paymentRepository.findAll(
                paymentSpecification, PageRequest.of(0, 10)
        );

        assertEquals(2, paymentsPage.getTotalElements());
    }

    @Test
    @DisplayName("Verify find by session ID")
    void getPaymentsBySessionId_returnsPayment() {
        String sessionId = "some-session-id-1";

        Optional<Payment> actual = paymentRepository.findBySessionId(sessionId);
        Long expectedRentalId = 1L;

        assertTrue(actual.isPresent());
        assertEquals(expectedRentalId, actual.get().getId());
    }
}