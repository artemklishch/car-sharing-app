package com.example.carsharingapp.service.telegram;

import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.model.Rental;
import java.util.List;

public interface TelegramNotificationService {
    void addCarNotification(CarResponseDto car);

    void updateCarNotification(CarResponseDto car);

    void deleteCarNotification(Long carId);

    void addRentalNotification(Rental rental, Car car);

    void returnRentalNotification(Rental rental, Car car);

    void paymentNotification(PaymentResponseDto toDto);

    void overdueNotification(List<Rental> rentals);
}
