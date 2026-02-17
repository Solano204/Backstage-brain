package neobank.dto;

import jakarta.validation.constraints.*;
import neobank.entity.Account;

public record AccountCreateRequest(
        @NotNull Account.AccountType type,
        @NotBlank @Size(min = 3, max = 3) String currency
) {}