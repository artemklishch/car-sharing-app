package com.example.carsharingapp.dto.car;

import com.example.carsharingapp.enums.ModelType;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private ModelType type;
    private int inventory;
    private BigDecimal dailyFee;
}
