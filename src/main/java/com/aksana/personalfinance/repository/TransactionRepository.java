package com.aksana.personalfinance.repository;

import com.aksana.personalfinance.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  TransactionRepository extends JpaRepository<Transaction, Long> {
}
