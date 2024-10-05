package com.example.carsharingapp.service;

import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.dto.user.UserUpdateProfileDto;
import com.example.carsharingapp.dto.user.UserUpdateRoleDto;
import com.example.carsharingapp.exceptions.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    UserResponseDto getUser(Long id);

    UserResponseDto updateRole(Long id, UserUpdateRoleDto requestDto);

    UserResponseDto updateUserProfile(Long id, UserUpdateProfileDto requestDto);
}
