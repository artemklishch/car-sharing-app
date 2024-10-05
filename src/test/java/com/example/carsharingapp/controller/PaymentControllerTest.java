package com.example.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.enums.PaymentStatus;
import com.example.carsharingapp.enums.PaymentType;
import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.repository.payment.PaymentRepository;
import com.example.carsharingapp.service.telegram.TelegramNotificationService;
import com.example.carsharingapp.utils.HandleDefaultDBPayments;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentControllerTest extends HandleDefaultDBPayments {
    private static final String ENDPOINT = "/payments";

    private static final String TEST_SESSION_ID = "some-session-id-4";

    protected static MockMvc mockMvc;

    private static PaymentResponseDto MOCKED_PAYMENT_RESPONSE_DTO;

    @MockBean
    private TelegramNotificationService telegramNotificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        MOCKED_PAYMENT_RESPONSE_DTO = new PaymentResponseDto()
                .setId(1L)
                .setStatus(PaymentStatus.PAID)
                .setType(PaymentType.PAYMENT)
                .setAmount(BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("Verify get payments by user ID")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Order(1)
    void getPaymentsByUserId() throws Exception {
        MvcResult result = mockMvc.perform(get(ENDPOINT)
                        .param("user_id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<PaymentResponseDto> payments = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<PaymentResponseDto>>() {
                }
        );

        assertEquals(2, payments.size());
    }

    @Test
    @DisplayName("Verify payment success")
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @Order(2)
    void getSuccessfulPayment() throws Exception {

        mockMvc.perform(get(ENDPOINT + "/success?session_id=" + TEST_SESSION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        doNothing().when(telegramNotificationService)
                .paymentNotification(MOCKED_PAYMENT_RESPONSE_DTO);

        Optional<Payment> payment = paymentRepository.findBySessionId(TEST_SESSION_ID);

        assertTrue(payment.isPresent());
        assertEquals(PaymentStatus.PAID, payment.get().getStatus());
    }

    @Test
    @DisplayName("Verify payment cancel")
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @Order(3)
    void getCanceledPayment() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/cancel?session_id=" + TEST_SESSION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<Payment> payment = paymentRepository.findBySessionId(TEST_SESSION_ID);

        assertTrue(payment.isPresent());
        assertEquals(PaymentStatus.CANCELED, payment.get().getStatus());
    }
}