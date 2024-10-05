package com.example.carsharingapp.dto.rental;

import com.example.carsharingapp.model.Car;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate = null;
    private Car car;
    private Long userId;
}
