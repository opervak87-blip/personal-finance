package com.aksana.personalfinance.controller;

import com.aksana.personalfinance.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final GreetingService greetingService;

    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/api/hello")
    public String sayHello() {
        return greetingService.getWelcomeMessage();
    }
}
