package com.example.carsharingapp.repository.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.utils.HandleDefaultDBUsers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends HandleDefaultDBUsers {
    private static final String EXISTING_EMAIL = "bob@gmail.com";
    private static final String EXISTING_NAME = "Bob";

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Check if user email exists")
    void checkIfEmailExists_existingEmail() {
        boolean actual = userRepository.existsByEmail(EXISTING_EMAIL);
        assertTrue(actual);
    }

    @Test
    @DisplayName("Check if not existing email returns false")
    void checkIfEmailExists_notExistingEmail() {
        boolean actual = userRepository.existsByEmail(
                "some-wrong-text" + EXISTING_EMAIL
        );
        assertFalse(actual);
    }

    @Test
    @DisplayName("Check if returns user by existing email")
    void returnUser_withExistingEmail() {
        Optional<User> actual = userRepository.findByEmail(EXISTING_EMAIL);
        assertTrue(actual.isPresent());
        assertEquals(actual.get().getFirstName(), EXISTING_NAME);
    }

    @Test
    @DisplayName("Check if returns NULL by not existing email")
    void returnUser_withNotExistingEmail() {
        Optional<User> actual = userRepository.findByEmail(
                "some-wrong-text" + EXISTING_EMAIL
        );
        assertEquals(actual, Optional.empty());
    }
}