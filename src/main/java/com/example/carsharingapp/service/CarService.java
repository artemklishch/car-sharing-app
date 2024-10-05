package com.example.carsharingapp.service;

import com.example.carsharingapp.dto.car.CarResponseDto;
import java.util.List;

import com.example.carsharingapp.dto.car.CreateCarRequestDto;
import com.example.carsharingapp.dto.car.UpdateCarRequestDto;
import org.springframework.data.domain.Pageable;

public interface CarService {
    List<CarResponseDto> findAll(Pageable pageable);

    CarResponseDto findById(Long id);

    CarResponseDto addCar(CreateCarRequestDto requestDto);

    CarResponseDto updateCar(Long id, UpdateCarRequestDto requestDto);

    void deleteCar(Long id);
}
