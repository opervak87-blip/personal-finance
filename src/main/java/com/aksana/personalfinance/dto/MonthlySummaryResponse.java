package com.aksana.personalfinance.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlySummaryResponse(YearMonth month,
                                     BigDecimal totalIncome,
                                     BigDecimal totalExpenses,
                                     BigDecimal netAmount) {
}
