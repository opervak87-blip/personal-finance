package com.aksana.personalfinance.service;

import com.aksana.personalfinance.dto.TransactionRequest;
import com.aksana.personalfinance.repository.AccountRepository;
import com.aksana.personalfinance.repository.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);

        transactionService = new TransactionService(
                transactionRepository,
                accountRepository
        );
    }

    @Test
    void shouldRejectTransactionForAnotherUsersAccount() {
        Long authenticatedUserId = 2L;
        Long anotherUsersAccountId = 10L;

        TransactionRequest request = new TransactionRequest(
                "Groceries",
                new java.math.BigDecimal("85.50"),
                "Food",
                "EXPENSE",
                anotherUsersAccountId
        );

        when(accountRepository.findByIdAndUserId(
                anotherUsersAccountId,
                authenticatedUserId
        )).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.createTransaction(
                        authenticatedUserId,
                        request
                )
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldReturnOnlyTransactionsForRequestedUser() {
        Long userId = 2L;

        when(transactionRepository.findByAccount_User_Id(userId))
                .thenReturn(java.util.List.of());

        var transactions =
                transactionService.getTransactionsForUser(userId);

        assertEquals(0, transactions.size());

        verify(transactionRepository)
                .findByAccount_User_Id(userId);
    }
}
