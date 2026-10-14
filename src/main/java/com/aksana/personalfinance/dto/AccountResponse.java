package com.aksana.personalfinance.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id,
        String name,
        String accountType,
        String currencyCode,
        BigDecimal openingBalance,
        Instant createdAt
) {
}
