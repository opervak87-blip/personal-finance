package com.aksana.personalfinance.service;

import com.aksana.personalfinance.entity.Transaction;
import com.aksana.personalfinance.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }


    public Transaction getTransactionById(Long id) {
        return findTransactionOrThrow(id);
    }

    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Transaction existing = findTransactionOrThrow(id);

        existing.setDescription(updatedTransaction.getDescription());
        existing.setAmount(updatedTransaction.getAmount());
        existing.setCategory(updatedTransaction.getCategory());
        existing.setType(updatedTransaction.getType());

        return transactionRepository.save(existing);
    }

    public void deleteTransaction(Long id) {
        Transaction existing = findTransactionOrThrow(id);
        transactionRepository.delete(existing);
    }

    private Transaction findTransactionOrThrow(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Transaction not found with id " + id
                ));
    }

}
