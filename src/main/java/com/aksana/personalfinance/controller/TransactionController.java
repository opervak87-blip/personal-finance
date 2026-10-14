package com.aksana.personalfinance.controller;

import com.aksana.personalfinance.dto.MonthlySummaryResponse;
import com.aksana.personalfinance.dto.TransactionRequest;
import com.aksana.personalfinance.dto.TransactionResponse;
import com.aksana.personalfinance.entity.Transaction;
import com.aksana.personalfinance.entity.User;
import com.aksana.personalfinance.service.TransactionService;
import com.aksana.personalfinance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(
            TransactionService transactionService,
            UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,

            Authentication authentication
    ) {
        User user = getAuthenticatedUser(authentication);

        if (from == null && to == null) {
            return transactionService.getTransactionsForUser(user.getId());
        }

        if (from == null || to == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Both 'from' and 'to' must be provided together"
            );
        }

        return transactionService.getTransactionsForUserBetween(
                user.getId(), from, to
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return transactionService.createTransaction(
                user.getId(),
                request
        );
    }


    @GetMapping("/summary")
    public MonthlySummaryResponse getMonthlySummary(
            @RequestParam String month,
            Authentication authentication
    ) {
        YearMonth yearMonth;

        try {
            yearMonth = YearMonth.parse(month);
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Month must use the format yyyy-MM, for example 2026-10"
            );
        }

        User user = getAuthenticatedUser(authentication);

        return transactionService.getMonthlySummary(
                user.getId(), yearMonth
        );
    }

    private User getAuthenticatedUser(Authentication authentication) {
        return userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found"
                ));
    }
}