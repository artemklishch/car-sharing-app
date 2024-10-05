package com.example.carsharingapp.service.telegram;

import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.repository.rental.RentalRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyNotificationServiceImpl {
    private final RentalRepository rentalRepository;
    private final TelegramNotificationService telegramNotificationService;

    @Scheduled(cron = "0 0 0 * * *")
    public void reportAboutOverdueRentals() {
        List<Rental> rentals = rentalRepository.findAllOverdue(LocalDate.now().minusDays(1));
        telegramNotificationService.overdueNotification(rentals);
    }
}
