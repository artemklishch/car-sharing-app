package com.example.carsharingapp.mapper.rental;

import com.example.carsharingapp.config.MapperConfig;
import com.example.carsharingapp.dto.rental.AddRentalDto;
import com.example.carsharingapp.dto.rental.AddedRentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.model.Rental;
import java.time.LocalDate;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDto toDto(Rental rental);

    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    AddedRentalResponseDto toAddedRentalResponseDto(Rental rental);

    @Mapping(target = "rentalDate", ignore = true)
    Rental toEntity(AddRentalDto requestDto);

    @AfterMapping
    default void setCar(@MappingTarget RentalResponseDto responseDto, Rental rental) {
        responseDto.setCar(rental.getCar());
    }

    @AfterMapping
    default void setRental(@MappingTarget Rental rental, AddRentalDto requestDto) {
        rental.setReturnDate(LocalDate.parse(requestDto.getReturnDate()));
        rental.setCar(new Car().setId(requestDto.getCarId()));
    }
}
