package com.aksana.personalfinance.repository;

import com.aksana.personalfinance.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface  TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccount_User_Id(Long userId);

    List<Transaction> findByAccount_User_IdAndTransactionDateBetween(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    );

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.account.user.id = :userId
      AND t.type = :type
      AND t.transactionDate >= :from
      AND t.transactionDate < :to
    """)
    BigDecimal sumAmountByUserAndType(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
