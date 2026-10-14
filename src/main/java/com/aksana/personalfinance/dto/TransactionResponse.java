package com.aksana.personalfinance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse( Long id,
                                   String description,
                                   BigDecimal amount,
                                   String category,
                                   String type,
                                   Long accountId,
                                   LocalDateTime transactionDate) {
}
