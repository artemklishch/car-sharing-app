package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.utils.Constants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentSearchParamsDto {
    @NotEmpty(message = "User ID is mandatory")
    @Pattern(
            regexp = Constants.PATTERN_ONLY_POSITIVE_NUMBERS,
            message = Constants.INVALID_POSITIVE_NUMBER
    )
    private String user_id;
}
