package com.example.carsharingapp.dto.car;

import com.example.carsharingapp.enums.ModelType;
import com.example.carsharingapp.validation.EnumMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCarRequestDto {
    @NotBlank(message = "Model field is mandatory")
    @Size(min = 1, max = 255, message = "Value can not be less 1 and over 255 characters")
    private String model;
    @NotBlank(message = "Brand field is mandatory")
    @Size(min = 1, max = 255, message = "Value can not be less 1 and over 255 characters")
    private String brand;
    @NotNull(message = "Model type field is mandatory")
    @EnumMatch(enumClass = ModelType.class)
    @Size(min = 1, max = 50, message = "Value can not be less 1 and over 50 characters")
    private String type;
    @PositiveOrZero(message = "Inventory value can not be less than 0")
    private int inventory;
    @PositiveOrZero(message = "Daily fee value can not be less than 0")
    private BigDecimal dailyFee;
}
