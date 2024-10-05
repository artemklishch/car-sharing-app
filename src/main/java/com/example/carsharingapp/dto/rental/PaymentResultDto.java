package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentResultDto {
    @NotEmpty(message = "Session ID is mandatory")
    private String session_id;
}
