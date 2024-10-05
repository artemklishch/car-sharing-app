package com.example.carsharingapp.service.telegram;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.enums.ModelType;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.carsharingapp.enums.PaymentStatus;
import com.example.carsharingapp.enums.PaymentType;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    private static Rental MOCKED_RENTAL;

    private static Car MOCKED_CAR;

    private static PaymentResponseDto MOCKED_PAYMENT_RESPONSE_DTO;

    @Mock
    private CarTelegramBot carTelegramBot;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "botChatId", "123456789");
        MOCKED_CAR = new Car()
                .setId(1L)
                .setInventory(10)
                .setType(ModelType.SUV)
                .setModel("Ford")
                .setBrand("Ford")
                .setDailyFee(BigDecimal.valueOf(200));
        MOCKED_RENTAL = new Rental()
                .setId(1L)
                .setRentalDate(LocalDate.now())
                .setCar(MOCKED_CAR)
                .setUser(new User())
                .setReturnDate(LocalDate.now().plusYears(1));
        MOCKED_PAYMENT_RESPONSE_DTO = new PaymentResponseDto()
                .setId(1L)
                .setStatus(PaymentStatus.PAID)
                .setType(PaymentType.PAYMENT)
                .setAmount(BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("Verify add car notification")
    void verifyAddCarNotification() {
        CarResponseDto car = new CarResponseDto()
                .setId(1L)
                .setModel("Ford")
                .setBrand("Ford")
                .setType(ModelType.SUV)
                .setInventory(20)
                .setDailyFee(BigDecimal.valueOf(200));

        notificationService.addCarNotification(car);

        verify(carTelegramBot).sendMessage(any(Long.class), any(String.class));
    }

    @Test
    @DisplayName("Verify update car notification")
    void verifyUpdateCarNotification() {
        CarResponseDto car = new CarResponseDto()
                .setId(1L)
                .setModel("Ford 111")
                .setBrand("Ford")
                .setType(ModelType.SUV)
                .setInventory(20)
                .setDailyFee(BigDecimal.valueOf(200));

        notificationService.updateCarNotification(car);

        verify(carTelegramBot).sendMessage(any(Long.class), any(String.class));
    }

    @Test
    @DisplayName("Verify delete car notification")
    void verifyDeleteCarNotification() {
        Long id = 1L;

        notificationService.deleteCarNotification(id);

        verify(carTelegramBot).sendMessage(any(Long.class), any(String.class));
    }

    @Test
    @DisplayName("Verify add rental notification")
    void verifyAddRentalNotification() {
        Rental rental = new Rental();

        notificationService.addRentalNotification(MOCKED_RENTAL, MOCKED_CAR);

        verify(carTelegramBot).sendMessage(any(Long.class), any(String.class));
    }

    @Test
    @DisplayName("Verify return rental notification")
    void verifyReturnRentalNotification() {
        notificationService.returnRentalNotification(MOCKED_RENTAL, MOCKED_CAR);

        verify(carTelegramBot).sendMessage(any(Long.class), any(String.class));
    }

    @Test
    @DisplayName("Verify payment notification")
    void verifyPaymentNotification() {
        notificationService.paymentNotification(MOCKED_PAYMENT_RESPONSE_DTO);

        verify(carTelegramBot).sendMessage(any(Long.class), any(String.class));
    }
}