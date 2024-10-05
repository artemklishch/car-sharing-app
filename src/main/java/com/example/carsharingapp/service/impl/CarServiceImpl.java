package com.example.carsharingapp.service.impl;

import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.car.CreateCarRequestDto;
import com.example.carsharingapp.dto.car.UpdateCarRequestDto;
import com.example.carsharingapp.enums.ModelType;
import com.example.carsharingapp.exceptions.ProceedingException;
import com.example.carsharingapp.mapper.car.CarMapper;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.service.CarService;
import com.example.carsharingapp.service.telegram.TelegramNotificationService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final TelegramNotificationService telegramNotificationService;

    @Override
    public List<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toCarResponseDto)
                .toList();
    }

    @Override
    public CarResponseDto findById(Long id) {
        return carMapper.toCarResponseDto(carRepository.findById(id).orElseThrow(
                () -> new ProceedingException("Car not found by ID: " + id)
        ));
    }

    @Override
    public CarResponseDto addCar(CreateCarRequestDto requestDto) {
        Car car = carMapper.toEntity(requestDto);
        carRepository.save(car);
        CarResponseDto responseDto = carMapper.toCarResponseDto(car);
        telegramNotificationService.addCarNotification(responseDto);
        return responseDto;
    }

    @Override
    public CarResponseDto updateCar(Long id, UpdateCarRequestDto requestDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new ProceedingException("Car not found by ID: " + id)
        );
        boolean isUpdated = isCarUpdated(car, requestDto);
        CarResponseDto responseDto = carMapper.toCarResponseDto(car);
        if (isUpdated) {
            telegramNotificationService.updateCarNotification(responseDto);
            return carMapper.toCarResponseDto(carRepository.save(car));
        }
        return responseDto;
    }

    protected boolean isCarUpdated(Car car, UpdateCarRequestDto requestDto) {
        String model = requestDto.getModel();
        String brand = requestDto.getBrand();
        String type = requestDto.getType();
        Integer inventory = requestDto.getInventory();
        BigDecimal dailyFee = requestDto.getDailyFee();

        boolean isUpdated = false;

        if (model != null && !model.equals(car.getModel())) {
            car.setModel(model);
            isUpdated = true;
        }
        if (brand != null && !brand.equals(car.getBrand())) {
            car.setBrand(brand);
            isUpdated = true;
        }
        if (type != null && !type.equals(car.getType().toString())) {
            car.setType(ModelType.valueOf(type));
            isUpdated = true;
        }
        if (inventory != null && inventory != car.getInventory()) {
            car.setInventory(inventory);
            isUpdated = true;
        }
        if (dailyFee != null && !dailyFee.equals(car.getDailyFee())) {
            car.setDailyFee(dailyFee);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.findById(id).orElseThrow(
                () -> new ProceedingException("Car not found by ID: " + id)
        );
        telegramNotificationService.deleteCarNotification(id);
        carRepository.deleteById(id);
    }
}
