package com.example.carsharingapp.service.telegram;

import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.model.Rental;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements TelegramNotificationService {
    @Value("${telegram.bot.chat.id}")
    private String botChatId;

    private final CarTelegramBot carTelegramBot;

    public void addCarNotification(CarResponseDto car) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("New car was added to the DB:")
                .append(System.lineSeparator())
                .append("Model: ")
                .append(car.getModel())
                .append(System.lineSeparator())
                .append("Brand: ")
                .append(car.getBrand())
                .append(System.lineSeparator())
                .append("Type: ")
                .append(car.getType())
                .append(System.lineSeparator())
                .append("Daily fee: ")
                .append(car.getDailyFee())
                .append(".");
        carTelegramBot.sendMessage(
                Long.parseLong(botChatId),
                notificationBuilder.toString()
        );
    }

    public void updateCarNotification(CarResponseDto car) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("The car with ID: ")
                .append(car.getId())
                .append(", - was updated")
                .append(System.lineSeparator())
                .append("Car: ")
                .append(car.getModel())
                .append(System.lineSeparator())
                .append("Model: ")
                .append(car.getModel())
                .append(System.lineSeparator())
                .append("Brand: ")
                .append(car.getBrand())
                .append(System.lineSeparator())
                .append("Type: ")
                .append(car.getType())
                .append(System.lineSeparator())
                .append("Daily fee: ")
                .append(car.getDailyFee())
                .append(".");
        carTelegramBot.sendMessage(
                Long.parseLong(botChatId),
                notificationBuilder.toString()
        );
    }

    public void deleteCarNotification(Long carId) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("The car with ID: ")
                .append(carId)
                .append(", - was deleted")
                .append(".");
        carTelegramBot.sendMessage(
                Long.parseLong(botChatId),
                notificationBuilder.toString()
        );
    }

    @Override
    public void addRentalNotification(Rental rental, Car car) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("New rent was ordered:")
                .append(System.lineSeparator())
                .append("Model: ")
                .append(car.getModel())
                .append(System.lineSeparator())
                .append("Brand: ")
                .append(car.getBrand())
                .append(System.lineSeparator())
                .append("Type: ")
                .append(car.getType())
                .append(System.lineSeparator())
                .append("Daily fee: ")
                .append(car.getDailyFee())
                .append(System.lineSeparator())
                .append("Rental date: ")
                .append(rental.getRentalDate())
                .append(System.lineSeparator())
                .append("Return date: ")
                .append(rental.getReturnDate())
                .append(".");
        carTelegramBot.sendMessage(
                Long.parseLong(botChatId),
                notificationBuilder.toString()
        );
    }

    @Override
    public void returnRentalNotification(Rental rental, Car car) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("Rented car was returned:")
                .append(System.lineSeparator())
                .append("Model: ")
                .append(car.getModel())
                .append(System.lineSeparator())
                .append("Brand: ")
                .append(car.getBrand())
                .append(System.lineSeparator())
                .append("Type: ")
                .append(car.getType())
                .append(System.lineSeparator())
                .append("Daily fee: ")
                .append(car.getDailyFee())
                .append(System.lineSeparator())
                .append("Rental date: ")
                .append(rental.getRentalDate())
                .append(System.lineSeparator())
                .append("Return date: ")
                .append(rental.getReturnDate())
                .append(System.lineSeparator())
                .append("Actual date of returning: ")
                .append(rental.getActualReturnDate())
                .append(".");
        carTelegramBot.sendMessage(
                Long.parseLong(botChatId),
                notificationBuilder.toString()
        );
    }

    @Override
    public void paymentNotification(PaymentResponseDto paymentDto) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("Payment: ")
                .append(System.lineSeparator())
                .append("Rental ID: ")
                .append(paymentDto.getRental())
                .append(System.lineSeparator())
                .append("Amount: ")
                .append(paymentDto.getAmount())
                .append(".");
        carTelegramBot.sendMessage(
                Long.parseLong(botChatId),
                notificationBuilder.toString()
        );
    }

    @Override
    public void overdueNotification(List<Rental> rentals) {
        StringBuilder notificationBuilder = new StringBuilder();
        if (rentals.isEmpty()) {
            notificationBuilder.append("No rentals overdue today!");
        } else {
            notificationBuilder.append("Rental overdue today (or upcoming overdue):")
                    .append(System.lineSeparator());
            for (Rental rental : rentals) {
                notificationBuilder.append("Rental ID: ")
                        .append(rental.getId())
                        .append(System.lineSeparator())
                        .append("Date of returning: ")
                        .append(rental.getReturnDate())
                        .append(System.lineSeparator())
                        .append("Car: ")
                        .append(rental.getCar().getModel())
                        .append(System.lineSeparator())
                        .append("Daily fee: ")
                        .append(rental.getCar().getDailyFee())
                        .append(System.lineSeparator())
                        .append("User ID: ")
                        .append(rental.getUser().getId())
                        .append(System.lineSeparator())
                        .append("User name: ")
                        .append(rental.getUser().getFirstName())
                        .append(" ")
                        .append(rental.getUser().getLastName());
            }
        }
        carTelegramBot.sendMessage(
                Long.parseLong(botChatId),
                notificationBuilder.toString()
        );
    }
}
