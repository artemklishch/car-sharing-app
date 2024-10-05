package com.example.carsharingapp.controller;

import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.car.CreateCarRequestDto;
import com.example.carsharingapp.dto.car.UpdateCarRequestDto;
import com.example.carsharingapp.service.CarService;
import jakarta.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping
    @Operation(summary = "Get all cars", description = "Get all cars")
    @ResponseStatus(HttpStatus.OK)
    public List<CarResponseDto> getCars(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get car", description = "Get car's detailed information")
    @ResponseStatus(HttpStatus.OK)
    public CarResponseDto getCar(@PathVariable Long id) {
        return carService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Add car", description = "Add new car to data")
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponseDto addCar(@RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.addCar(requestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Update car", description = "Update car data by ID")
    @ResponseStatus(HttpStatus.OK)
    public CarResponseDto updateCar(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCarRequestDto requestDto
    ) {
        return carService.updateCar(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Delete car", description = "Delete car data by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
