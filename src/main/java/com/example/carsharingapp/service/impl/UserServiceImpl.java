package com.example.carsharingapp.service.impl;

import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.dto.user.UserUpdateProfileDto;
import com.example.carsharingapp.dto.user.UserUpdateRoleDto;
import com.example.carsharingapp.enums.RoleName;
import com.example.carsharingapp.exceptions.ProceedingException;
import com.example.carsharingapp.exceptions.RegistrationException;
import com.example.carsharingapp.mapper.user.UserMapper;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.repository.user.UserRepository;
import com.example.carsharingapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    "Can't register user with email "
                            + requestDto.getEmail()
                            + ". Email is already in use."
            );
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getUser(Long id) {
        return userMapper.toUserResponseDto(
                userRepository.findById(id).orElseThrow(
                        () -> new ProceedingException("User not found by id: " + id)
                )
        );
    }

    @Override
    public UserResponseDto updateRole(Long id, UserUpdateRoleDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ProceedingException("User not found by id: " + id)
        );
        String userRole = String.valueOf(user.getRole());
        String updatedRoleValue = requestDto.role();
        if (userRole.equals(updatedRoleValue)) {
            return userMapper.toUserResponseDto(user);
        }
        user.setRole(RoleName.valueOf(requestDto.role()));
        userRepository.save(user);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateUserProfile(
            Long id, UserUpdateProfileDto requestDto
    ) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ProceedingException("User not found by id: " + id)
        );
        boolean isUserUpdated = updatedUser(user, requestDto);
        if (!isUserUpdated) {
            return userMapper.toUserResponseDto(user);
        }
        userRepository.save(user);
        return userMapper.toUserResponseDto(user);
    }

    private boolean updatedUser(User user, UserUpdateProfileDto requestDto) {
        String firstName = requestDto.getFirstName();
        String lastName = requestDto.getLastName();
        boolean noUpdates = (firstName == null || firstName.equals(user.getFirstName())) &&
                (lastName == null || lastName.equals(user.getLastName()));
        if (noUpdates) {
            return false;
        }
        if (firstName != null && !firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
        }
        if (lastName != null && !lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
        }
        return true;
    }
}
