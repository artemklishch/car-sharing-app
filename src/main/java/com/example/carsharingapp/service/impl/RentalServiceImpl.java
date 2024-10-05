package com.example.carsharingapp.service.impl;

import com.example.carsharingapp.dto.rental.AddRentalDto;
import com.example.carsharingapp.dto.rental.AddedRentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.example.carsharingapp.exceptions.ProceedingException;
import com.example.carsharingapp.mapper.rental.RentalMapper;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.repository.rental.RentalRepository;
import com.example.carsharingapp.repository.rental.RentalSpecificationBuilder;
import com.example.carsharingapp.service.RentalService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;
    private final CarRepository carRepository;

    @Override
    public RentalResponseDto getRentalByIdByManager(Long id) {
        return rentalMapper.toDto(rentalRepository.findById(id).orElseThrow(
                () -> new ProceedingException("Rental not found by ID: " + id)
        ));
    }

    @Override
    public RentalResponseDto getRentalByIdByCustomer(Long id, User user) {
        return rentalMapper.toDto(rentalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(
                        () -> new ProceedingException("Rental not found by ID: " + id)
                ));
    }

    @Override
    public List<RentalResponseDto> searchRentalsByManager(
            RentalSearchParametersDto params, Pageable pageable
    ) {
        Specification<Rental> rentalSpecification = rentalSpecificationBuilder.build(params);
        return rentalRepository.findAll(rentalSpecification, pageable)
                .stream().map(rentalMapper::toDto).toList();
    }

    @Override
    public List<RentalResponseDto> searchRentalsByCustomer(
            RentalSearchParametersDto requestDto, Pageable pageable, User user
    ) {
        return rentalRepository.findAllByUserId(user.getId(), pageable)
                .stream().map(rentalMapper::toDto).toList();
    }

    @Transactional
    @Override
    public Map<String, Object> addRental(AddRentalDto requestDto, User user) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new ProceedingException(
                        "Impossible to rent the car. Car not found by ID: " + requestDto.getCarId()
                )
        );
        int inventoryValue = car.getInventory();
        if (inventoryValue == 0) {
            throw new ProceedingException("Impossible to rent the car. No cars available.");
        }
        car.setInventory(inventoryValue - 1);
        carRepository.save(car);
        Rental rental = rentalMapper.toEntity(requestDto);
        rental.setUser(user);
        AddedRentalResponseDto responseDto = rentalMapper.toAddedRentalResponseDto(rentalRepository.save(rental));
        HashMap<String, Object> resultData = new HashMap<>();
        resultData.put("responseDto", responseDto);
        resultData.put("rental", rental);
        resultData.put("car", car);
        return resultData;
    }

    @Transactional
    @Override
    public Map<String, Object> returnRental(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new ProceedingException("Rental not found by ID: " + id)
        );
        if (rental.getActualReturnDate() != null) {
            throw new ProceedingException("Rental with ID: " + id + " already returned.");
        }
        Long carId = rental.getCar().getId();
        Car car = carRepository.findById(carId).orElseThrow(
                () -> new ProceedingException("Car not found by ID: " + carId)
        );
        rental.setActualReturnDate(LocalDate.now());
        car.setInventory(car.getInventory() + 1);
        rentalRepository.save(rental);
        carRepository.save(car);
        HashMap<String, Object> resultData = new HashMap<>();
        resultData.put("rental", rental);
        resultData.put("car", car);
        return resultData;
    }
}
