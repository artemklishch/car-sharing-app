package com.example.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.car.CreateCarRequestDto;
import com.example.carsharingapp.dto.car.UpdateCarRequestDto;
import com.example.carsharingapp.enums.ModelType;
import com.example.carsharingapp.exceptions.ProceedingException;
import com.example.carsharingapp.service.telegram.TelegramNotificationService;
import com.example.carsharingapp.utils.HandleDefaultDBCars;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CarControllerTest extends HandleDefaultDBCars {
    private static final String ENDPOINT = "/cars";

    protected static MockMvc mockMvc;

    private static CarResponseDto mockedCarResponseDto;

    @MockBean
    private TelegramNotificationService telegramNotificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        mockedCarResponseDto = new CarResponseDto()
                .setId(4L)
                .setModel("Ford 102")
                .setBrand("Ford Corporation")
                .setInventory(1)
                .setType(ModelType.SEDAN)
                .setDailyFee(BigDecimal.valueOf(2));
    }

    @Test
    @DisplayName("Verify getting all cars")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Order(1)
    void getAllCars_returnsCarResponseDtoList() throws Exception {
        MvcResult result = mockMvc.perform(get(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CarResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                List.class
        );

        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    @DisplayName("Verify getting car by ID")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Order(2)
    void getCarById_returnsCarResponseDto() throws Exception {
        Long id = 1L;
        String expected = "BMW-600";
        MvcResult result = mockMvc.perform(get(ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarResponseDto.class
        );

        assertNotNull(actual);
        assertEquals(expected, actual.getModel());
    }

    @Test
    @DisplayName("Verify creating the car")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Order(3)
    void createCar_returnsCarResponseDto() throws Exception {
        CreateCarRequestDto requestDto = new CreateCarRequestDto()
                .setModel("Ford 102")
                .setBrand("Ford Corporation")
                .setInventory(1)
                .setType("SEDAN")
                .setDailyFee(BigDecimal.valueOf(2));
        doNothing().when(telegramNotificationService).addCarNotification(mockedCarResponseDto);
        String jsonObject = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post(ENDPOINT)
                        .content(jsonObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        MvcResult finalResult = mockMvc.perform(get(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CarResponseDto> finalList = objectMapper.readValue(
                finalResult.getResponse().getContentAsString(),
                List.class
        );
        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarResponseDto.class
        );

        assertNotNull(actual);
        assertEquals(requestDto.getModel(), actual.getModel());
        assertEquals(3, finalList.size());
    }

    @Test
    @DisplayName("Verify update car with wrong ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Order(4)
    void updateCar_withWrongID_throwsException() throws Exception {
        Long wrongId = 100L;
        int updatedInventory = 100;
        UpdateCarRequestDto requestDto = new UpdateCarRequestDto()
                .setInventory(updatedInventory);
        String jsonObject = objectMapper.writeValueAsString(requestDto);

        Exception exception = assertThrows(
                Exception.class,
                () -> mockMvc.perform(put(ENDPOINT + "/" + wrongId)
                                .content(jsonObject)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andReturn()
        );

        Throwable cause = exception.getCause();
        assertInstanceOf(ProceedingException.class, cause);
        assertEquals(
                "Car not found by ID: " + wrongId,
                cause.getMessage()
        );
    }

    @Test
    @DisplayName("Verify update car")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Order(5)
    void updateCar_byID_returnsCarResponseDro() throws Exception {
        Long id = 1L;
        int updatedInventory = 10;
        UpdateCarRequestDto requestDto = new UpdateCarRequestDto()
                .setInventory(updatedInventory);
        doNothing().when(telegramNotificationService).addCarNotification(mockedCarResponseDto);
        String jsonObject = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put(ENDPOINT + "/" + id)
                        .content(jsonObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CarResponseDto.class
        );

        assertNotNull(actual);
        assertEquals(updatedInventory, actual.getInventory());
    }

    @Test
    @DisplayName("Verify update car when no updates")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Order(6)
    void updateCar_byID_returnsNoChangedCarResponseDto() throws Exception {
        Long id = 1L;
        MvcResult existingResult = mockMvc.perform(get(ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarResponseDto existingCar = objectMapper.readValue(
                existingResult.getResponse().getContentAsString(), CarResponseDto.class
        );
        UpdateCarRequestDto requestDto = new UpdateCarRequestDto();
        String jsonObject = objectMapper.writeValueAsString(requestDto);
        MvcResult updatedResult = mockMvc.perform(put(ENDPOINT + "/" + id)
                        .content(jsonObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarResponseDto carAfterUpdate = objectMapper.readValue(
                updatedResult.getResponse().getContentAsString(), CarResponseDto.class
        );

        assertNotNull(carAfterUpdate);
        assertEquals(existingCar, carAfterUpdate);
    }

    @Test
    @DisplayName("Verify deleting car when wrong ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Order(7)
    void deleteCar_withNoValidId_throwsException() {
        Long wrongId = 100L;
        Exception exception = assertThrows(
                Exception.class,
                () -> mockMvc.perform(delete(ENDPOINT + "/" + wrongId))
        );

        Throwable cause = exception.getCause();
        assertInstanceOf(ProceedingException.class, cause);
        assertEquals(
                "Car not found by ID: " + wrongId,
                cause.getMessage()
        );
    }

    @Test
    @DisplayName("Verify deleting car")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Order(8)
    void deleteCar_withValidId() throws Exception {
        Long id = 1L;
        doNothing().when(telegramNotificationService).addCarNotification(mockedCarResponseDto);
        MvcResult startResult = mockMvc.perform(get(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<CarResponseDto> startCars = objectMapper.readValue(
                startResult.getResponse().getContentAsString(),
                List.class
        );

        MvcResult result = mockMvc.perform(delete(ENDPOINT + "/" + id))
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult finalResult = mockMvc.perform(get(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<CarResponseDto> finalCars = objectMapper.readValue(
                finalResult.getResponse().getContentAsString(),
                List.class
        );

        assertNotNull(result);
        assertTrue(startCars.size() > finalCars.size());
    }

    @Test
    @DisplayName("Verify deleting car that was deleted")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Order(9)
    void deleteCar_thatWsDeleted_throwsException() {
        Long id = 1L;
        Exception exception = assertThrows(
                Exception.class,
                () -> mockMvc.perform(delete(ENDPOINT + "/" + id))
        );

        Throwable cause = exception.getCause();
        assertInstanceOf(ProceedingException.class, cause);
        assertEquals(
                "Car not found by ID: " + id,
                cause.getMessage()
        );
    }
}