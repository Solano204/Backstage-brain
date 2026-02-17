package neobank.dto;


import neobank.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountNumber,
        Account.AccountType type,
        BigDecimal balance,
        String currency,
        boolean active,
        LocalDateTime createdAt
) {}