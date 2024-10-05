package com.example.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingapp.dto.payment.CreatePaymentDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.enums.PaymentStatus;
import com.example.carsharingapp.enums.PaymentType;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.repository.payment.PaymentRepository;
import com.example.carsharingapp.security.CustomUserDetailsService;
import com.example.carsharingapp.service.PaymentService;
import com.example.carsharingapp.utils.HandleDefaultDBPayments;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class PaymentControllerMockStripeTest extends HandleDefaultDBPayments {
    private static final String ENDPOINT = "/payments";

    private static final String TEST_SESSION_ID = "some-session-id-4";

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    protected static MockMvc mockMvc;

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
    }

    @Test
    @DisplayName("Verify create payment session")
    @WithMockUser(username = "user", roles = "USER")
    void createPaymentSession_returnsPaymentResponseDto() throws Exception {
        // arrange
        CreatePaymentDto requestDto = new CreatePaymentDto()
                .setAmount(BigDecimal.valueOf(1200)) // $12.00
                .setPaymentType(String.valueOf(PaymentType.PAYMENT))
                .setRentalId(1L);
        PaymentResponseDto responseDto = new PaymentResponseDto()
                .setId(1L)
                .setType(PaymentType.PAYMENT)
                .setRental(1L)
                .setStatus(PaymentStatus.PAID)
                .setSessionId(TEST_SESSION_ID);

        // mock user
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getId()).thenReturn(1L);

        // mock user service that returns user
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUser);

        // mock payment service
        when(paymentService.createSession(any(CreatePaymentDto.class), anyLong()))
                .thenReturn(responseDto);

        // execute
        MvcResult result = mockMvc.perform(post(ENDPOINT)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PaymentResponseDto actualResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto.class
        );

        // check
        assertEquals(TEST_SESSION_ID, actualResponseDto.getSessionId());
    }
}