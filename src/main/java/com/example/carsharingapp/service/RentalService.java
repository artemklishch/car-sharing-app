package com.example.carsharingapp.service;

import com.example.carsharingapp.dto.rental.AddRentalDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.example.carsharingapp.model.User;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto getRentalByIdByManager(Long id);

    RentalResponseDto getRentalByIdByCustomer(Long id, User user);

    List<RentalResponseDto> searchRentalsByManager(
            RentalSearchParametersDto requestDto, Pageable pageable
    );

    List<RentalResponseDto> searchRentalsByCustomer(
            RentalSearchParametersDto requestDto, Pageable pageable, User user
    );

    Map<String, Object> addRental(AddRentalDto requestDto, User user);

    Map<String, Object> returnRental(Long id);
}
