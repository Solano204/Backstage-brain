package neobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neobank.dto.TransactionRequest;
import neobank.entity.Account;
import neobank.entity.Transaction;
import neobank.exception.InsufficientFundsException;
import neobank.exception.InvalidTransactionException;
import neobank.exception.ResourceNotFoundException;
import neobank.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional
    public Transaction createTransaction(TransactionRequest request) {
        log.info("Creating transaction from {} to {}", request.fromAccountNumber(), request.toAccountNumber());

        Account fromAccount = accountService.findByAccountNumber(request.fromAccountNumber());
        Account toAccount = accountService.findByAccountNumber(request.toAccountNumber());

        validateTransaction(fromAccount, toAccount, request.amount());

        String transactionId = UUID.randomUUID().toString();

        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .type(request.type())
                .amount(request.amount())
                .currency(fromAccount.getCurrency())
                .description(request.description())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .status(Transaction.TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.amount()));
        fromAccount.setUpdatedAt(LocalDateTime.now());

        toAccount.setBalance(toAccount.getBalance().add(request.amount()));
        toAccount.setUpdatedAt(LocalDateTime.now());

        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public Transaction findByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));
    }

    public List<Transaction> findAccountTransactions(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount, String description) {
        log.info("Processing deposit to account: {}", accountNumber);

        Account account = accountService.findByAccountNumber(accountNumber);

        if (!account.isActive()) {
            throw new InvalidTransactionException("Cannot deposit to inactive account");
        }

        String transactionId = UUID.randomUUID().toString();

        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .type(Transaction.TransactionType.DEPOSIT)
                .amount(amount)
                .currency(account.getCurrency())
                .description(description)
                .toAccount(account)
                .status(Transaction.TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();

        account.setBalance(account.getBalance().add(amount));
        account.setUpdatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(String accountNumber, BigDecimal amount, String description) {
        log.info("Processing withdrawal from account: {}", accountNumber);

        Account account = accountService.findByAccountNumber(accountNumber);

        if (!account.isActive()) {
            throw new InvalidTransactionException("Cannot withdraw from inactive account");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        String transactionId = UUID.randomUUID().toString();

        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .type(Transaction.TransactionType.WITHDRAWAL)
                .amount(amount)
                .currency(account.getCurrency())
                .description(description)
                .fromAccount(account)
                .status(Transaction.TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();

        account.setBalance(account.getBalance().subtract(amount));
        account.setUpdatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    private void validateTransaction(Account fromAccount, Account toAccount, BigDecimal amount) {
        if (!fromAccount.isActive() || !toAccount.isActive()) {
            throw new InvalidTransactionException("Cannot transact with inactive accounts");
        }

        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new InvalidTransactionException("Currency mismatch between accounts");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account");
        }
    }
}