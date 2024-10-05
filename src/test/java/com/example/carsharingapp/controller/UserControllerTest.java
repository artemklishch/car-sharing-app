package com.example.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingapp.utils.HandleDefaultDBUsers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class UserControllerTest extends HandleDefaultDBUsers {
    private static final String ENDPOINT = "/users";

    protected static MockMvc mockMvc;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Should return 401 Unauthorized for unauthenticated users")
    void getUser_shouldReturnUnauthorized_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized for unauthenticated users")
    void updateRole_shouldReturnUnauthorized_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/" + 1L + "/rol"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized for unauthenticated users")
    void updateUser_shouldReturnUnauthorized_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/me"))
                .andExpect(status().isUnauthorized());
    }
}