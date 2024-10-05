package com.example.carsharingapp.mapper.payment;

import com.example.carsharingapp.config.MapperConfig;
import com.example.carsharingapp.dto.payment.CreatePaymentDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.model.Rental;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rental", source = "payment.rental.id")
    @Mapping(target = "sessionId", source = "payment.sessionId")
    @Mapping(target = "sessionUrl", source = "payment.session")
    PaymentResponseDto toDto(Payment payment);

    @Mapping(target = "type", source = "paymentType")
    Payment toEntity(CreatePaymentDto requestDto);
    @AfterMapping
    default void setRental(@MappingTarget Payment payment, CreatePaymentDto requestDto) {
        payment.setRental(new Rental().setId(requestDto.getRentalId()));
    }
}
