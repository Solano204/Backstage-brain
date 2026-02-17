package neobank.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DepositRequest(
        @NotBlank String accountNumber,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String description
) {}