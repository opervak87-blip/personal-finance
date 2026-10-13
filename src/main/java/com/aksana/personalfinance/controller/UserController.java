package com.aksana.personalfinance.controller;

import com.aksana.personalfinance.dto.UserRegistrationRequest;
import com.aksana.personalfinance.dto.UserResponse;
import com.aksana.personalfinance.entity.User;
import com.aksana.personalfinance.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        User user = userService.registerUser(
                request.getEmail(),
                request.getPassword()
        );

        return new UserResponse(user.getId(), user.getEmail());
    }
}
