package com.example.carsharingapp.mapper.car;

import com.example.carsharingapp.config.MapperConfig;
import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.car.CreateCarRequestDto;
import com.example.carsharingapp.model.Car;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarResponseDto toCarResponseDto(Car car);

    Car toEntity(CreateCarRequestDto requestDto);
}
