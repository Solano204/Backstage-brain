package neobank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import neobank.dto.AccountCreateRequest;
import neobank.dto.AccountResponse;
import neobank.entity.Account;
import neobank.service.AccountService;
import neobank.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;

    @Operation(
            summary = "Create a new account",
            description = "Creates a new bank account for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 1,
                        "accountNumber": "123456789012",
                        "type": "CHECKING",
                        "balance": 0.00,
                        "currency": "USD",
                        "active": true,
                        "createdAt": "2024-02-16T10:30:00"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing token"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Account creation details",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AccountCreateRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Checking Account",
                                    value = """
                    {
                        "type": "CHECKING",
                        "currency": "USD"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "Savings Account",
                                    value = """
                    {
                        "type": "SAVINGS",
                        "currency": "USD"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "Investment Account",
                                    value = """
                    {
                        "type": "INVESTMENT",
                        "currency": "EUR"
                    }
                    """
                            )
                    }
            )
    )
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountCreateRequest request,
            Authentication authentication) {

        var user = userService.findByEmail(authentication.getName());
        Account account = accountService.createAccount(user.getId(), request);

        return ResponseEntity.ok(mapToResponse(account));
    }

    @Operation(
            summary = "Get all user accounts",
            description = "Retrieves all accounts belonging to the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "id": 1,
                            "accountNumber": "123456789012",
                            "type": "CHECKING",
                            "balance": 1500.50,
                            "currency": "USD",
                            "active": true,
                            "createdAt": "2024-02-16T10:30:00"
                        },
                        {
                            "id": 2,
                            "accountNumber": "987654321098",
                            "type": "SAVINGS",
                            "balance": 5000.00,
                            "currency": "USD",
                            "active": true,
                            "createdAt": "2024-02-16T11:00:00"
                        }
                    ]
                    """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getUserAccounts(Authentication authentication) {
        var user = userService.findByEmail(authentication.getName());
        List<Account> accounts = accountService.findUserAccounts(user.getId());

        return ResponseEntity.ok(accounts.stream()
                .map(this::mapToResponse)
                .toList());
    }

    @Operation(
            summary = "Get account by number",
            description = "Retrieves account details by account number"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 1,
                        "accountNumber": "123456789012",
                        "type": "CHECKING",
                        "balance": 1500.50,
                        "currency": "USD",
                        "active": true,
                        "createdAt": "2024-02-16T10:30:00"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                        "status": 404,
                        "message": "Account not found: 999999999999",
                        "timestamp": "2024-02-16T10:30:00"
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(description = "12-digit account number", example = "123456789012")
            @PathVariable String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(mapToResponse(account));
    }

    @Operation(
            summary = "Deactivate account",
            description = "Deactivates an account (soft delete)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Account deactivated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deactivateAccount(
            @Parameter(description = "Account ID", example = "1")
            @PathVariable Long accountId) {
        accountService.deactivateAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    private AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getType(),
                account.getBalance(),
                account.getCurrency(),
                account.isActive(),
                account.getCreatedAt()
        );
    }
}