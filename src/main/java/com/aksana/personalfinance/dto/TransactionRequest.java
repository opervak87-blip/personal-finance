package com.aksana.personalfinance.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionRequest(@NotBlank(message = "Description is required")
                                  @Size(max = 255, message = "Description cannot exceed 255 characters")
                                  String description,

                                 @NotNull(message = "Amount is required")
                                  @DecimalMin(value = "0.01", message = "Amount must be positive")
                                 BigDecimal amount,

                                 @NotBlank(message = "Category is required")
                                  @Size(max = 100, message = "Category cannot exceed 100 characters")
                                  String category,

                                 @NotBlank(message = "Type is required")
                                  @Pattern(regexp = "INCOME|EXPENSE",
                                          message = "Type must be INCOME or EXPENSE")
                                  String type,

                                 @NotNull(message = "Account ID is required")
                                  @Positive(message = "Account ID must be positive")
                                  Long accountId) {
}
