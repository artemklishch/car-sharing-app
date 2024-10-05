package com.example.carsharingapp.dto.car;

import com.example.carsharingapp.enums.ModelType;
import com.example.carsharingapp.validation.EnumMatch;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateCarRequestDto {
    @Size(min = 1, max = 255, message = "Value can not be less 1 and over 255 characters")
    private String model;
    @Size(min = 1, max = 255, message = "Value can not be less 1 and over 255 characters")
    private String brand;
    @EnumMatch(enumClass = ModelType.class)
    @Size(min = 1, max = 50, message = "Value can not be less 1 and over 50 characters")
    private String type;
    @PositiveOrZero(message = "Inventory value can not be less than 0")
    private Integer inventory;
    @PositiveOrZero(message = "Daily fee value can not be less than 0")
    private BigDecimal dailyFee;
}
