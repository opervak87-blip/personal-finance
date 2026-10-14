package com.aksana.personalfinance.service;

import com.aksana.personalfinance.dto.MonthlySummaryResponse;
import com.aksana.personalfinance.dto.TransactionRequest;
import com.aksana.personalfinance.dto.TransactionResponse;
import com.aksana.personalfinance.entity.Account;
import com.aksana.personalfinance.entity.Transaction;
import com.aksana.personalfinance.repository.AccountRepository;
import com.aksana.personalfinance.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsForUser(Long userId) {
        return transactionRepository.findByAccount_User_Id(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsForUserBetween(
            Long userId,
            java.time.LocalDateTime from,
            java.time.LocalDateTime to
    ) {
        if (from.isAfter(to)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "'from' must be before or equal to 'to'"
            );
        }

        return transactionRepository
                .findByAccount_User_IdAndTransactionDateBetween(userId, from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TransactionResponse createTransaction(
            Long userId,
            TransactionRequest request) {

        Account account = accountRepository
                .findByIdAndUserId(request.accountId(), userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                ));

        Transaction transaction = new Transaction();
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setCategory(request.category());
        transaction.setType(request.type());
        transaction.setAccount(account);

        Transaction saved = transactionRepository.save(transaction);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(
            Long userId,
            YearMonth month
    ) {
        LocalDateTime from = month.atDay(1).atStartOfDay();
        LocalDateTime to = month.plusMonths(1).atDay(1).atStartOfDay();

        BigDecimal income = transactionRepository.sumAmountByUserAndType(
                userId, "INCOME", from, to
        );

        BigDecimal expenses = transactionRepository.sumAmountByUserAndType(
                userId, "EXPENSE", from, to
        );

        BigDecimal netAmount = income.subtract(expenses);

        return new MonthlySummaryResponse(
                month,
                income,
                expenses,
                netAmount
        );
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getType(),
                transaction.getAccount().getId(),
                transaction.getTransactionDate()
        );
    }
}