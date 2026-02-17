package neobank.dto;

import jakarta.validation.constraints.*;
import neobank.entity.Transaction;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull Transaction.TransactionType type,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String fromAccountNumber,
        @NotBlank String toAccountNumber,
        String description
) {}