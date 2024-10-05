package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.enums.PaymentStatus;
import com.example.carsharingapp.enums.PaymentType;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentResponseDto {
    private Long id;
    private PaymentStatus status;
    private PaymentType type;
    private Long rental;
    private BigDecimal amount;
    private String sessionId;
    private String sessionUrl;
}
