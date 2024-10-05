package com.example.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingapp.dto.user.UserLoginRequestDto;
import com.example.carsharingapp.dto.user.UserLoginResponseDto;
import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.utils.HandleDefaultDBUsers;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class AuthenticationControllerTest extends HandleDefaultDBUsers {
    private static final String ENDPOINT = "/auth";

    private static final String DEFAULT_EMAIL = "bob@gmail.com";

    private static final String DEFAULT_PASSWORD = "12345678";

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Verify login")
    void verifyLogin() throws Exception {
        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto(DEFAULT_EMAIL, DEFAULT_PASSWORD);
        String jsonRequest = objectMapper.writeValueAsString(userLoginRequestDto);

        MvcResult result = mockMvc.perform(post(ENDPOINT + "/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserLoginResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class
        );

        assertNotNull(actual);
        Field field = Arrays.stream(actual.getClass().getDeclaredFields()).toList().get(0);
        assertEquals(actual.getClass().getDeclaredFields().length, 1);
        assertEquals(field.getName(), "token");
    }

    @Test
    @DisplayName("Verify login when incorrect password")
    void verifyLogin_withInvalidPassword() throws Exception {
        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto(
                DEFAULT_EMAIL, "invalid_password"
        );
        String jsonRequest = objectMapper.writeValueAsString(userLoginRequestDto);

        MvcResult mvcResult = mockMvc.perform(post(ENDPOINT + "/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(response.getErrorMessage(),"Unauthorized");
        assertEquals(response.getStatus(), 401);
    }

    @Test
    @DisplayName("Verify register user")
    void verifyRegisterUser_returnUserResponseDto() throws Exception {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto()
                .setEmail("new-user@gmail.com")
                .setPassword(DEFAULT_PASSWORD)
                .setRepeatPassword(DEFAULT_PASSWORD)
                .setFirstName("SomeName")
                .setLastName("SomeLastName")
                .setRole("CUSTOMER");
        String jsonRequest = objectMapper.writeValueAsString(userRegistrationRequestDto);

        MvcResult result = mockMvc.perform(post(ENDPOINT + "/register")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );
        assertNotNull(actual);
        assertEquals(actual.getEmail(), userRegistrationRequestDto.getEmail());
    }

    @Test
    @DisplayName("Verify error when try to register user with the existing email")
    void verifyRegisterUser_withExistingEmail() throws Exception {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto()
                .setEmail(DEFAULT_EMAIL)
                .setPassword(DEFAULT_PASSWORD)
                .setRepeatPassword(DEFAULT_PASSWORD)
                .setFirstName("SomeName")
                .setLastName("SomeLastName")
                .setRole("CUSTOMER");
        String jsonRequest = objectMapper.writeValueAsString(userRegistrationRequestDto);

        MvcResult mvcResult = mockMvc.perform(post(ENDPOINT + "/register")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        assertTrue(responseContent.contains("Can't register user with email "
                + userRegistrationRequestDto.getEmail()
                + ". Email is already in use."));
    }
}