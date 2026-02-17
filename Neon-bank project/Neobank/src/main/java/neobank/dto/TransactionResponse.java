package neobank.dto;


import neobank.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String transactionId,
        Transaction.TransactionType type,
        BigDecimal amount,
        String currency,
        Transaction.TransactionStatus status,
        String description,
        String fromAccountNumber,
        String toAccountNumber,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {}