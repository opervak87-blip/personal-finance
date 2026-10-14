package com.aksana.personalfinance.service;

import com.aksana.personalfinance.dto.MonthlySummaryResponse;
import com.aksana.personalfinance.repository.AccountRepository;
import com.aksana.personalfinance.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class MonthlySummaryServiceTest {

    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);

        transactionService = new TransactionService(
                transactionRepository,
                mock(AccountRepository.class)
        );
    }

    @Test
    void shouldCalculateMonthlyIncomeExpensesAndNetAmount() {
        Long userId = 1L;
        YearMonth month = YearMonth.of(2026, 10);

        when(transactionRepository.sumAmountByUserAndType(
                userId,
                "INCOME",
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0)
        )).thenReturn(new BigDecimal("4500.00"));

        when(transactionRepository.sumAmountByUserAndType(
                userId,
                "EXPENSE",
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0)
        )).thenReturn(new BigDecimal("1250.50"));

        MonthlySummaryResponse summary =
                transactionService.getMonthlySummary(userId, month);

        assertEquals(month, summary.month());
        assertEquals(0, new BigDecimal("4500.00")
                .compareTo(summary.totalIncome()));
        assertEquals(0, new BigDecimal("1250.50")
                .compareTo(summary.totalExpenses()));
        assertEquals(0, new BigDecimal("3249.50")
                .compareTo(summary.netAmount()));

        verify(transactionRepository).sumAmountByUserAndType(
                userId, "INCOME",
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0)
        );
    }

    @Test
    void shouldReturnZeroTotalsWhenThereAreNoTransactions() {
        Long userId = 1L;
        YearMonth month = YearMonth.of(2026, 10);

        when(transactionRepository.sumAmountByUserAndType(
                org.mockito.ArgumentMatchers.eq(userId),
                org.mockito.ArgumentMatchers.eq("INCOME"),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(BigDecimal.ZERO);

        when(transactionRepository.sumAmountByUserAndType(
                org.mockito.ArgumentMatchers.eq(userId),
                org.mockito.ArgumentMatchers.eq("EXPENSE"),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(BigDecimal.ZERO);

        MonthlySummaryResponse summary =
                transactionService.getMonthlySummary(userId, month);

        assertEquals(0, summary.totalIncome().compareTo(BigDecimal.ZERO));
        assertEquals(0, summary.totalExpenses().compareTo(BigDecimal.ZERO));
        assertEquals(0, summary.netAmount().compareTo(BigDecimal.ZERO));
    }
}
