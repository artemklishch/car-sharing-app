package com.example.carsharingapp.dto.rental;

import com.example.carsharingapp.utils.Constants;
import com.example.carsharingapp.validation.FutureLocalDate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddRentalDto {
    @NotEmpty(message = "Return date of rental is mandatory")
    @FutureLocalDate
    @Pattern(
            regexp = Constants.PATTERN_LOCAL_DATE,
            message = Constants.INVALID_LOCAL_DATE
    )
    private String returnDate;
    @NotNull(message = "Car ID is mandatory")
    @Positive(message = Constants.INVALID_POSITIVE_NUMBER)
    private Long carId;
}
