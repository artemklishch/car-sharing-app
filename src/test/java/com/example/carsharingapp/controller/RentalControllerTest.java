package com.example.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingapp.enums.ModelType;
import com.example.carsharingapp.enums.RoleName;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.service.telegram.TelegramNotificationService;
import com.example.carsharingapp.utils.HandleDefaultDBRentals;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class RentalControllerTest extends HandleDefaultDBRentals {
    private static final String ENDPOINT = "/rentals";

    private final static LocalDate NOW_DATE = LocalDate.now();

    protected static MockMvc mockMvc;

    private static Rental MOCKED_RENTAL;

    private static Car MOCKED_CAR;

    @MockBean
    private TelegramNotificationService telegramNotificationService;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        User user = new User()
                .setId(1L).setRole(RoleName.CUSTOMER)
                .setEmail("bob@gmail.com")
                .setPassword("$2a$10$MftFdz42cvwYagDZhqRVb.tqq/1iIFaYRxOvEOhltQ/AseB6RES3O")
                .setFirstName("Bob")
                .setLastName("Marley");
        MOCKED_CAR = new Car()
                .setId(1L)
                .setInventory(10)
                .setType(ModelType.SUV)
                .setModel("Ford")
                .setBrand("Ford")
                .setDailyFee(BigDecimal.valueOf(200));
        MOCKED_RENTAL = new Rental()
                .setId(1L)
                .setRentalDate(NOW_DATE)
                .setCar(MOCKED_CAR)
                .setUser(user)
                .setReturnDate(LocalDate.now().plusYears(1));
    }

    @Test
    @DisplayName("Return rental")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Order(2)
    void returnRental() throws Exception {
        doNothing().when(telegramNotificationService)
                .addRentalNotification(MOCKED_RENTAL, MOCKED_CAR);
        MvcResult result = mockMvc.perform(post(ENDPOINT + "/1/return")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result);
    }
}