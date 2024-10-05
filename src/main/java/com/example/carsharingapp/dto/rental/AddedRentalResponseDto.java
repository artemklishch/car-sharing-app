package com.example.carsharingapp.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddedRentalResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate = null;
    private Long carId;
    private Long userId;
}
