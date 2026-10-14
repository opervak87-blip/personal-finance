package com.aksana.personalfinance.controller;

import com.aksana.personalfinance.dto.AccountResponse;
import com.aksana.personalfinance.entity.Account;
import com.aksana.personalfinance.entity.User;
import com.aksana.personalfinance.service.AccountService;
import com.aksana.personalfinance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(
            AccountService accountService,
            UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return accountService.getAccountsForUser(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(
            @Valid @RequestBody Account account,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Account savedAccount = accountService.createAccount(
                user.getId(),
                account
        );

        return toResponse(savedAccount);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        return userService.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Authenticated user not found"));
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getAccountType().name(),
                account.getCurrencyCode(),
                account.getOpeningBalance(),
                account.getCreatedAt()
        );
    }
}
