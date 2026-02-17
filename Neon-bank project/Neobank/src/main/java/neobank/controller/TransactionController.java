package neobank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import neobank.dto.DepositRequest;
import neobank.dto.TransactionRequest;
import neobank.dto.TransactionResponse;
import neobank.dto.WithdrawalRequest;
import neobank.entity.Transaction;
import neobank.service.TransactionService;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(
            summary = "Create a transfer",
            description = "Transfers money between two accounts"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transfer completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 1,
                        "transactionId": "550e8400-e29b-41d4-a716-446655440000",
                        "type": "TRANSFER",
                        "amount": 100.00,
                        "currency": "USD",
                        "status": "COMPLETED",
                        "description": "Payment for services",
                        "fromAccountNumber": "123456789012",
                        "toAccountNumber": "987654321098",
                        "createdAt": "2024-02-16T10:30:00",
                        "completedAt": "2024-02-16T10:30:01"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Insufficient funds or invalid transaction",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                        "status": 400,
                        "message": "Insufficient funds in account",
                        "timestamp": "2024-02-16T10:30:00"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Transfer details",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TransactionRequest.class),
                    examples = @ExampleObject(
                            value = """
                {
                    "type": "TRANSFER",
                    "amount": 100.00,
                    "fromAccountNumber": "123456789012",
                    "toAccountNumber": "987654321098",
                    "description": "Payment for services"
                }
                """
                    )
            )
    )
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> createTransfer(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(request);
        return ResponseEntity.ok(mapToResponse(transaction));
    }

    @Operation(
            summary = "Deposit money",
            description = "Deposits money into an account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 2,
                        "transactionId": "650e8400-e29b-41d4-a716-446655440001",
                        "type": "DEPOSIT",
                        "amount": 500.00,
                        "currency": "USD",
                        "status": "COMPLETED",
                        "description": "Salary deposit",
                        "fromAccountNumber": null,
                        "toAccountNumber": "123456789012",
                        "createdAt": "2024-02-16T10:30:00",
                        "completedAt": "2024-02-16T10:30:00"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Deposit details",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DepositRequest.class),
                    examples = @ExampleObject(
                            value = """
                {
                    "accountNumber": "123456789012",
                    "amount": 500.00,
                    "description": "Salary deposit"
                }
                """
                    )
            )
    )
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
        Transaction transaction = transactionService.deposit(
                request.accountNumber(),
                request.amount(),
                request.description()
        );
        return ResponseEntity.ok(mapToResponse(transaction));
    }

    @Operation(
            summary = "Withdraw money",
            description = "Withdraws money from an account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Withdrawal successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 3,
                        "transactionId": "750e8400-e29b-41d4-a716-446655440002",
                        "type": "WITHDRAWAL",
                        "amount": 200.00,
                        "currency": "USD",
                        "status": "COMPLETED",
                        "description": "ATM withdrawal",
                        "fromAccountNumber": "123456789012",
                        "toAccountNumber": null,
                        "createdAt": "2024-02-16T10:30:00",
                        "completedAt": "2024-02-16T10:30:00"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Insufficient funds",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                        "status": 400,
                        "message": "Insufficient funds",
                        "timestamp": "2024-02-16T10:30:00"
                    }
                    """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Withdrawal details",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = WithdrawalRequest.class),
                    examples = @ExampleObject(
                            value = """
                {
                    "accountNumber": "123456789012",
                    "amount": 200.00,
                    "description": "ATM withdrawal"
                }
                """
                    )
            )
    )
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawalRequest request) {
        Transaction transaction = transactionService.withdraw(
                request.accountNumber(),
                request.amount(),
                request.description()
        );
        return ResponseEntity.ok(mapToResponse(transaction));
    }

    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves transaction details by transaction ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 1,
                        "transactionId": "550e8400-e29b-41d4-a716-446655440000",
                        "type": "TRANSFER",
                        "amount": 100.00,
                        "currency": "USD",
                        "status": "COMPLETED",
                        "description": "Payment for services",
                        "fromAccountNumber": "123456789012",
                        "toAccountNumber": "987654321098",
                        "createdAt": "2024-02-16T10:30:00",
                        "completedAt": "2024-02-16T10:30:01"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found"
            )
    })
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(description = "Transaction UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String transactionId) {
        Transaction transaction = transactionService.findByTransactionId(transactionId);
        return ResponseEntity.ok(mapToResponse(transaction));
    }

    @Operation(
            summary = "Get account transactions",
            description = "Retrieves all transactions for a specific account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "id": 1,
                            "transactionId": "550e8400-e29b-41d4-a716-446655440000",
                            "type": "TRANSFER",
                            "amount": 100.00,
                            "currency": "USD",
                            "status": "COMPLETED",
                            "description": "Payment",
                            "fromAccountNumber": "123456789012",
                            "toAccountNumber": "987654321098",
                            "createdAt": "2024-02-16T10:30:00",
                            "completedAt": "2024-02-16T10:30:01"
                        },
                        {
                            "id": 2,
                            "transactionId": "650e8400-e29b-41d4-a716-446655440001",
                            "type": "DEPOSIT",
                            "amount": 500.00,
                            "currency": "USD",
                            "status": "COMPLETED",
                            "description": "Salary",
                            "fromAccountNumber": null,
                            "toAccountNumber": "123456789012",
                            "createdAt": "2024-02-16T09:00:00",
                            "completedAt": "2024-02-16T09:00:00"
                        }
                    ]
                    """
                            )
                    )
            )
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @Parameter(description = "Account ID", example = "1")
            @PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.findAccountTransactions(accountId);
        return ResponseEntity.ok(transactions.stream()
                .map(this::mapToResponse)
                .toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getFromAccount() != null ? transaction.getFromAccount().getAccountNumber() : null,
                transaction.getToAccount() != null ? transaction.getToAccount().getAccountNumber() : null,
                transaction.getCreatedAt(),
                transaction.getCompletedAt()
        );
    }
}