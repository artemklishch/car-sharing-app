package com.example.carsharingapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.carsharingapp.dto.rental.AddRentalDto;
import com.example.carsharingapp.dto.rental.AddedRentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.enums.ModelType;
import com.example.carsharingapp.enums.RoleName;
import com.example.carsharingapp.exceptions.ProceedingException;
import com.example.carsharingapp.mapper.rental.RentalMapper;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.repository.rental.RentalRepository;
import com.example.carsharingapp.service.telegram.TelegramNotificationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    private static final int INVENTORY_NUMBER = 10;

    private final static LocalDate NOW_DATE = LocalDate.now();

    private static User TEST_CUSTOMER;

    private static Car TEST_CAR;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private TelegramNotificationService telegramNotificationService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private Rental testRental;

    private RentalResponseDto testRentalResponseDto;

    private AddedRentalResponseDto addedRentalResponseDto;

    private AddRentalDto testAddRentalDto;

    @BeforeAll
    static void beforeAll() {
        TEST_CUSTOMER = new User()
                .setId(1L)
                .setFirstName("TestCustomer Name")
                .setLastName("TestCustomer LastName")
                .setEmail("test-customer@example.com")
                .setPassword("password")
                .setRole(RoleName.CUSTOMER);
        TEST_CAR = new Car()
                .setId(1L)
                .setInventory(INVENTORY_NUMBER)
                .setType(ModelType.SUV)
                .setBrand("Audi")
                .setModel("Audi 100")
                .setDailyFee(BigDecimal.valueOf(12.50));
    }

    @BeforeEach
    void beforeEach() {
        TEST_CAR.setInventory(INVENTORY_NUMBER);
        testAddRentalDto = new AddRentalDto()
                .setReturnDate(String.valueOf(NOW_DATE.plusYears(1)))
                .setCarId(1L);
        testRental = new Rental()
                .setId(1L)
                .setRentalDate(NOW_DATE)
                .setReturnDate(LocalDate.parse(testAddRentalDto.getReturnDate()))
                .setCar(TEST_CAR)
                .setUser(TEST_CUSTOMER);
        testRentalResponseDto = new RentalResponseDto()
                .setId(testRental.getId())
                .setRentalDate(testRental.getRentalDate())
                .setReturnDate(testRental.getReturnDate())
                .setActualReturnDate(testRental.getActualReturnDate())
                .setCar(testRental.getCar())
                .setUserId(testRental.getUser().getId());
        addedRentalResponseDto = new AddedRentalResponseDto()
                .setId(testRental.getId())
                .setRentalDate(testRental.getRentalDate())
                .setReturnDate(testRental.getReturnDate())
                .setActualReturnDate(testRental.getActualReturnDate())
                .setCarId(testRental.getCar().getId())
                .setUserId(testRental.getUser().getId());
        ;
    }

    @Test
    @DisplayName("Verify get rental by ID by customer")
    void getRentalById_byCustomer_returnsRentalResponseDto() {
        when(rentalRepository.findByIdAndUserId(testRental.getId(), TEST_CUSTOMER.getId()))
                .thenReturn(Optional.of(testRental));
        when(rentalMapper.toDto(testRental)).thenReturn(testRentalResponseDto);

        RentalResponseDto actual = rentalService.getRentalByIdByCustomer(testRental.getId(), TEST_CUSTOMER);

        assertNotNull(actual);
        assertEquals(testRental.getId(), actual.getId());
    }

    @Test
    @DisplayName("Verify get rental by ID by manager")
    void getRentalById_byManager_returnsRentalResponseDto() {
        when(rentalRepository.findById(testRental.getId()))
                .thenReturn(Optional.of(testRental));
        when(rentalMapper.toDto(testRental)).thenReturn(testRentalResponseDto);

        RentalResponseDto actual = rentalService.getRentalByIdByManager(testRental.getId());

        assertNotNull(actual);
        assertEquals(testRental.getId(), actual.getId());
    }

    @Test
    @DisplayName("Verify get rental by ID, when not found, by customer")
    void getRentalById_byCustomer_whenNotFound_throwsException() {
        when(rentalRepository.findByIdAndUserId(testRental.getId(), TEST_CUSTOMER.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> rentalService.getRentalByIdByCustomer(testRental.getId(), TEST_CUSTOMER)
        );

        assertEquals(
                "Rental not found by ID: " + testRental.getId(),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Verify get rental by ID, when not found, by manager")
    void getRentalById_byManager_whenNotFound_throwsException() {
        when(rentalRepository.findById(testRental.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> rentalService.getRentalByIdByManager(testRental.getId())
        );

        assertEquals(
                "Rental not found by ID: " + testRental.getId(),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Verify add rental when car not found")
    void addRental_whenCarNotFound_throwsException() {
        when(carRepository.findById(testAddRentalDto.getCarId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> rentalService.addRental(testAddRentalDto, TEST_CUSTOMER)
        );

        assertEquals(
                "Impossible to rent the car. Car not found by ID: " +
                        testAddRentalDto.getCarId(),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Verify add rental when no car inventory")
    void addRental_whenNoCarInventory_throwsException() {
        TEST_CAR.setInventory(0);
        when(carRepository.findById(testAddRentalDto.getCarId()))
                .thenReturn(Optional.of(TEST_CAR));

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> rentalService.addRental(testAddRentalDto, TEST_CUSTOMER)
        );

        assertEquals(
                "Impossible to rent the car. No cars available.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Verify add rental")
    void addRental_returnsAddedRentalResponseDto() {
        when(rentalMapper.toEntity(testAddRentalDto)).thenReturn(testRental);
        when(carRepository.findById(testAddRentalDto.getCarId()))
                .thenReturn(Optional.of(TEST_CAR));
        when(rentalRepository.save(testRental)).thenReturn(testRental);
        when(rentalMapper.toAddedRentalResponseDto(testRental)).thenReturn(addedRentalResponseDto);

        Map<String, Object> actual = rentalService.addRental(testAddRentalDto, TEST_CUSTOMER);

        Car car = (Car) actual.get("car");
        assertNotNull(actual);
        assertEquals(testAddRentalDto.getCarId(), car.getId());
        assertEquals(INVENTORY_NUMBER, TEST_CAR.getInventory() + 1);
    }

    @Test
    @DisplayName("Verify return rental when rental not found")
    void returnRental_whenRentalNotFound_throwsException() {
        when(rentalRepository.findById(testRental.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> rentalService.getRentalByIdByManager(testRental.getId())
        );

        assertEquals(
                "Rental not found by ID: " + testRental.getId(),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Verify return rental when car not found")
    void returnRental_notContentReturned() {
        when(rentalRepository.findById(testRental.getId()))
                .thenReturn(Optional.of(testRental));
        when(carRepository.findById(testRental.getCar().getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> rentalService.returnRental(testRental.getId())
        );

        assertEquals(
                "Car not found by ID: " + TEST_CAR.getId(),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Verify return rental")
    void returnRental_noContentReturned() {
        when(rentalRepository.findById(testRental.getId()))
                .thenReturn(Optional.of(testRental));
        when(carRepository.findById(testRental.getCar().getId()))
                .thenReturn(Optional.of(TEST_CAR));

        rentalService.returnRental(testRental.getId());

        assertEquals(TEST_CAR.getInventory() - 1, INVENTORY_NUMBER);
        assertEquals(testRental.getActualReturnDate(), NOW_DATE);
    }
}