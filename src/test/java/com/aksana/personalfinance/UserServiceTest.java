package com.aksana.personalfinance;

import com.aksana.personalfinance.entity.User;
import com.aksana.personalfinance.exception.DuplicateEmailException;
import com.aksana.personalfinance.repository.UserRepository;
import com.aksana.personalfinance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerUserShouldStoreHashedPassword() {
        String rawPassword = "MySecurePassword123";

        when(userRepository.findByEmail("aksana@example.com"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerUser(
                "Aksana@Example.com",
                rawPassword
        );

        assertEquals("aksana@example.com", registeredUser.getEmail());
        assertNotEquals(rawPassword, registeredUser.getPasswordHash());
        assertTrue(passwordEncoder.matches(
                rawPassword,
                registeredUser.getPasswordHash()
        ));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserShouldRejectDuplicateEmail() {
        User existingUser = new User();
        existingUser.setEmail("aksana@example.com");

        when(userRepository.findByEmail("aksana@example.com"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.registerUser(
                        "Aksana@Example.com",
                        "MySecurePassword123"
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }
}
