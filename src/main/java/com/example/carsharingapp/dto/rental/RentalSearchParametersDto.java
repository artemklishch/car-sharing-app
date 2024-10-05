package com.example.carsharingapp.dto.rental;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalSearchParametersDto {
    private String user_id;
    private String is_active;
}
