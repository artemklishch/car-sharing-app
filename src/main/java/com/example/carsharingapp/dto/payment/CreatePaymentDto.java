package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.utils.Constants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePaymentDto {
    @NotBlank(message = "Payment type field is mandatory")
    @Pattern(
            regexp = "PAYMENT|FINE",
            message = "Payment type can be only 'PAYMENT' or 'FINE'"
    )
    private String paymentType;
    @NotNull(message = "Rental ID is mandatory")
    @Positive(message = Constants.INVALID_POSITIVE_NUMBER)
    private Long rentalId;
    @NotNull(message = "Amount field is mandatory")
    @Min(value = 50, message = "Amount should be at least 50 cents")
    private BigDecimal amount;
}
