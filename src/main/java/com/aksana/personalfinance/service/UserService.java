package com.aksana.personalfinance.service;

import com.aksana.personalfinance.entity.User;
import com.aksana.personalfinance.exception.DuplicateEmailException;
import com.aksana.personalfinance.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email));
    }

    public User registerUser(String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new DuplicateEmailException(
                    "An account with this email already exists");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        return userRepository.save(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
