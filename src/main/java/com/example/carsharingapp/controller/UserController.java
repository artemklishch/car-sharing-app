package com.example.carsharingapp.controller;

import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.dto.user.UserUpdateProfileDto;
import com.example.carsharingapp.dto.user.UserUpdateRoleDto;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.security.CustomUserDetailsService;
import com.example.carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get authorized user", description = "Get authorized user")
    public UserResponseDto getUser(Authentication authentication) {
        Long userId = ((User) userDetailsService.loadUserByUsername(authentication.getName()))
                .getId();
        return userService.getUser(userId);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Update user role", description = "Update user role")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUserRole(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRoleDto requestDto
    ) {
        return userService.updateRole(id, requestDto);
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Update user profile", description = "Update user profile")
    public UserResponseDto updateUserProfile(
            @RequestBody @Valid UserUpdateProfileDto requestDto,
            Authentication authentication
    ) {
        Long userId = ((User) userDetailsService.loadUserByUsername(authentication.getName()))
                .getId();
        return userService.updateUserProfile(userId, requestDto);
    }
}
