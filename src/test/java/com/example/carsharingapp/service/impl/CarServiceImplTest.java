package com.example.carsharingapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.car.CreateCarRequestDto;
import com.example.carsharingapp.dto.car.UpdateCarRequestDto;
import com.example.carsharingapp.enums.ModelType;
import com.example.carsharingapp.exceptions.ProceedingException;
import com.example.carsharingapp.mapper.car.CarMapper;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.service.telegram.TelegramNotificationService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @Mock
    private TelegramNotificationService telegramNotificationService;

    @InjectMocks
    private CarServiceImpl carService;

    private CreateCarRequestDto createCarRequestDto;

    private Car testCar;

    private CarResponseDto carResponseDto;

    @BeforeEach
    void beforeEach() {
        createCarRequestDto = new CreateCarRequestDto()
                .setModel("Ford 100")
                .setBrand("Ford Corporation")
                .setType("SEDAN")
                .setInventory(1)
                .setDailyFee(BigDecimal.valueOf(8));
        testCar = new Car()
                .setId(3L)
                .setModel(createCarRequestDto.getModel())
                .setBrand(createCarRequestDto.getBrand())
                .setType(ModelType.valueOf(createCarRequestDto.getType()))
                .setInventory(createCarRequestDto.getInventory())
                .setDailyFee(createCarRequestDto.getDailyFee());
        carResponseDto = new CarResponseDto()
                .setId(testCar.getId())
                .setModel(testCar.getModel())
                .setBrand(testCar.getBrand())
                .setType(testCar.getType())
                .setInventory(testCar.getInventory())
                .setDailyFee(testCar.getDailyFee());
    }

    @Test
    @DisplayName("Verify find all")
    void findAll_returnCarResponseDtoList() {
        Page<Car> carPage = new PageImpl<>(Collections.singletonList(testCar));
        when(carRepository.findAll(PageRequest.of(1, 10)))
                .thenReturn(carPage);
        when(carMapper.toCarResponseDto(testCar)).thenReturn(carResponseDto);

        List<CarResponseDto> cars = carService.findAll(PageRequest.of(1, 10));

        assertEquals(1, cars.size());
        assertEquals(carResponseDto, cars.get(0));
    }

    @Test
    @DisplayName("Verify find by wrong ID")
    void findByWrongId_throwsException() {
        Long wrongId = 100L;
        when(carRepository.findById(wrongId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> carService.findById(wrongId)
        );

        assertEquals("Car not found by ID: " + wrongId, exception.getMessage());
    }

    @Test
    @DisplayName("Verify find by ID")
    void findById_returnCarResponseDto() {
        when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
        when(carMapper.toCarResponseDto(testCar)).thenReturn(carResponseDto);

        CarResponseDto actual = carService.findById(testCar.getId());

        assertEquals(carResponseDto, actual);
    }

    @Test
    @DisplayName("Verify creating car")
    void createCar_returnCarResponseDto() {
        when(carMapper.toEntity(createCarRequestDto)).thenReturn(testCar);
        when(carRepository.save(testCar)).thenReturn(testCar);
        when(carMapper.toCarResponseDto(testCar)).thenReturn(carResponseDto);
        doNothing().when(telegramNotificationService).addCarNotification(carResponseDto);

        CarResponseDto carDto = carService.addCar(createCarRequestDto);

        assertEquals(carResponseDto, carDto);
    }

    @Test
    @DisplayName("Verify updating car with wrong ID")
    void updateCarByWrongId_throwsException() {
        Long wrongId = 100L;
        when(carRepository.findById(wrongId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> carService.findById(wrongId)
        );

        assertEquals("Car not found by ID: " + wrongId, exception.getMessage());
    }

    @Test
    @DisplayName("Verify updating car")
    void updateCar_returnCarResponseDto() {
        int updatedInventory = 10;
        UpdateCarRequestDto requestDto = new UpdateCarRequestDto()
                .setInventory(updatedInventory);
        carResponseDto.setInventory(updatedInventory);
        when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
        when(carRepository.save(testCar)).thenReturn(testCar);
        when(carMapper.toCarResponseDto(testCar)).thenReturn(carResponseDto);

        CarResponseDto carDto = carService.updateCar(testCar.getId(), requestDto);

        assertEquals(carDto.getInventory(), updatedInventory);
    }

    @Test
    @DisplayName("Verify nothing updated when empty DTO")
    void updateUser_withEmptyDTO_returnsFalse() {
        UpdateCarRequestDto requestDto = new UpdateCarRequestDto();

        boolean isUpdated = carService.isCarUpdated(testCar, requestDto);

        assertFalse(isUpdated);
    }

    @Test
    @DisplayName("Verify updated when update inventory")
    void updateUser_withNoEmpty_returnsTrue() {
        UpdateCarRequestDto requestDto = new UpdateCarRequestDto()
                .setInventory(10);

        boolean isUpdated = carService.isCarUpdated(testCar, requestDto);

        assertTrue(isUpdated);
    }

    @Test
    @DisplayName("Verify delete car with wrong ID")
    void deleteCarByWrongId_throwsException() {
        Long wrongId = 100L;
        when(carRepository.findById(wrongId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> carService.deleteCar(wrongId)
        );

        assertEquals("Car not found by ID: " + wrongId, exception.getMessage());
    }

    @Test
    @DisplayName("Verify deleting of the deleted car")
    void deleteOfDeletedCar_throwsException() {
        testCar.setDeleted(true);
        when(carRepository.findById(testCar.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> carService.deleteCar(testCar.getId())
        );

        assertEquals(
                "Car not found by ID: " + testCar.getId(),
                exception.getMessage()
        );
    }
}