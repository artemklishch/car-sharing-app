package com.example.carsharingapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    private UserRegistrationRequestDto registrationRequestDto;

    private UserResponseDto userResponseDto;

    private static final String ENCODED_PASSWORD = "encoded password";

    @BeforeEach
    void beforeEach() {
        registrationRequestDto = new UserRegistrationRequestDto()
                .setEmail("test1@email.com")
                .setPassword("password")
                .setRepeatPassword("password")
                .setFirstName("Test FirstName")
                .setLastName("Test LastName")
                .setRole(RoleName.CUSTOMER.toString());
        testUser = new User()
                .setId(1L)
                .setEmail(registrationRequestDto.getEmail())
                .setPassword(ENCODED_PASSWORD)
                .setFirstName(registrationRequestDto.getFirstName())
                .setLastName(registrationRequestDto.getLastName())
                .setRole(RoleName.valueOf(registrationRequestDto.getRole()));
        userResponseDto = new UserResponseDto()
                .setId(testUser.getId())
                .setEmail(testUser.getEmail())
                .setFirstName(testUser.getFirstName())
                .setLastName(testUser.getLastName())
                .setRole(testUser.getRole());
    }

    @Test
    @DisplayName("Verify register user")
    void registerUser_returnsUserResponseDto() throws RegistrationException {
        when(userRepository.existsByEmail(registrationRequestDto.getEmail()))
                .thenReturn(false);
        when(userMapper.toModel(registrationRequestDto)).thenReturn(testUser);
        when(passwordEncoder.encode(registrationRequestDto.getPassword())).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toUserResponseDto(testUser)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.register(registrationRequestDto);

        assertEquals(actual.getEmail(), registrationRequestDto.getEmail());
    }

    @Test
    @DisplayName("Verify error when registering user with existing email")
    void registerUser_withExistingEmail_throwsException() {
        when(userRepository.existsByEmail(registrationRequestDto.getEmail()))
                .thenReturn(true);

        Exception exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(registrationRequestDto)
        );

        assertEquals(
                exception.getMessage(),
                "Can't register user with email " +
                        registrationRequestDto.getEmail() +
                        ". Email is already in use."
        );
    }

    @Test
    @DisplayName("Verify getting user with valid ID")
    void gettingUser_withValidId_returnsUserResponseDto() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.ofNullable(testUser));
        when(userMapper.toUserResponseDto(testUser)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.getUser(testUser.getId());

        assertEquals(actual.getEmail(), testUser.getEmail());
    }

    @Test
    @DisplayName("Verify getting error with invalid ID")
    void gettingUser_withInvalidId_throwsException() {
        Long invalidId = 100L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> userService.getUser(invalidId)
        );

        assertEquals(exception.getMessage(), "User not found by id: " + invalidId);
    }

    @Test
    @DisplayName("Verify updating user role")
    void updateRole_returnsUserResponseDto() {
        UserUpdateRoleDto testUpdatedRoleDto = new UserUpdateRoleDto(RoleName.MANAGER.toString());
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.ofNullable(testUser));
        userResponseDto.setRole(RoleName.valueOf(testUpdatedRoleDto.role()));
        when(userMapper.toUserResponseDto(testUser)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.updateRole(testUser.getId(), testUpdatedRoleDto);

        assertEquals(actual.getRole(), testUser.getRole());
    }

    @Test
    @DisplayName("Verify error when updating user role with invalid ID")
    void updateRole_withInvalidId_throwsException() {
        UserUpdateRoleDto testUpdatedRoleDto = new UserUpdateRoleDto(RoleName.MANAGER.toString());
        Long invalidId = 100L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> userService.updateRole(invalidId, testUpdatedRoleDto)
        );

        assertEquals(exception.getMessage(), "User not found by id: " + invalidId);
    }

    @Test
    @DisplayName("Update user with invalid ID")
    void updateUser_withInvalidId_throwsException() {
        UserUpdateProfileDto testUpdateUserProfileDto = new UserUpdateProfileDto();
        Long invalidId = 100L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProceedingException.class,
                () -> userService.updateUserProfile(invalidId, testUpdateUserProfileDto)
        );

        assertEquals(exception.getMessage(), "User not found by id: " + invalidId);
    }

    @Test
    @DisplayName("Update user with valid ID")
    void updateUser_withValidId_returnsUserResponseDto() {
        UserUpdateProfileDto testUpdateUserProfileDto = new UserUpdateProfileDto();
        testUpdateUserProfileDto.setFirstName("Updated firstname");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.ofNullable(testUser));
        userResponseDto.setFirstName(testUpdateUserProfileDto.getFirstName());
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toUserResponseDto(testUser)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.updateUserProfile(testUser.getId(), testUpdateUserProfileDto);

        assertEquals(actual.getFirstName(), testUpdateUserProfileDto.getFirstName());
    }
}