package com.neobank.service;

import neobank.dto.TransactionRequest;
import neobank.entity.Account;
import neobank.entity.Transaction;
import neobank.entity.User;
import neobank.exception.InsufficientFundsException;
import neobank.exception.InvalidTransactionException;
import neobank.repository.TransactionRepository;
import neobank.service.AccountService;
import neobank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_Success() {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();

        Account fromAccount = Account.builder()
                .accountNumber("111111111111")
                .type(Account.AccountType.CHECKING)
                .balance(new BigDecimal("1000.00"))
                .user(user)
                .build();

        Account toAccount = Account.builder()
                .accountNumber("222222222222")
                .type(Account.AccountType.SAVINGS)
                .balance(new BigDecimal("500.00"))
                .user(user)
                .build();

        TransactionRequest request = new TransactionRequest(
                Transaction.TransactionType.TRANSFER,
                new BigDecimal("100.00"),
                "111111111111",
                "222222222222",
                "Test transfer"
        );

        when(accountService.findByAccountNumber("111111111111")).thenReturn(fromAccount);
        when(accountService.findByAccountNumber("222222222222")).thenReturn(toAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction result = transactionService.createTransaction(request);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals(Transaction.TransactionStatus.COMPLETED, result.getStatus());
        assertEquals(new BigDecimal("900.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("600.00"), toAccount.getBalance());
    }

    @Test
    void createTransaction_InsufficientFunds() {
        User user = User.builder().email("test@example.com").build();

        Account fromAccount = Account.builder()
                .accountNumber("111111111111")
                .type(Account.AccountType.CHECKING)
                .balance(new BigDecimal("50.00"))
                .user(user)
                .build();

        Account toAccount = Account.builder()
                .accountNumber("222222222222")
                .type(Account.AccountType.SAVINGS)
                .user(user)
                .build();

        TransactionRequest request = new TransactionRequest(
                Transaction.TransactionType.TRANSFER,
                new BigDecimal("100.00"),
                "111111111111",
                "222222222222",
                "Test transfer"
        );

        when(accountService.findByAccountNumber("111111111111")).thenReturn(fromAccount);
        when(accountService.findByAccountNumber("222222222222")).thenReturn(toAccount);

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.createTransaction(request)
        );
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_InactiveAccount() {
        User user = User.builder().email("test@example.com").build();

        Account fromAccount = Account.builder()
                .accountNumber("111111111111")
                .type(Account.AccountType.CHECKING)
                .active(false)
                .user(user)
                .build();

        Account toAccount = Account.builder()
                .accountNumber("222222222222")
                .type(Account.AccountType.SAVINGS)
                .user(user)
                .build();

        TransactionRequest request = new TransactionRequest(
                Transaction.TransactionType.TRANSFER,
                new BigDecimal("100.00"),
                "111111111111",
                "222222222222",
                "Test transfer"
        );

        when(accountService.findByAccountNumber("111111111111")).thenReturn(fromAccount);
        when(accountService.findByAccountNumber("222222222222")).thenReturn(toAccount);

        assertThrows(InvalidTransactionException.class, () ->
                transactionService.createTransaction(request)
        );
    }

    @Test
    void deposit_Success() {
        User user = User.builder().email("test@example.com").build();

        Account account = Account.builder()
                .accountNumber("111111111111")
                .type(Account.AccountType.CHECKING)
                .balance(new BigDecimal("1000.00"))
                .user(user)
                .build();

        when(accountService.findByAccountNumber("111111111111")).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction result = transactionService.deposit("111111111111", new BigDecimal("500.00"), "Deposit");

        assertNotNull(result);
        assertEquals(Transaction.TransactionType.DEPOSIT, result.getType());
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals(new BigDecimal("1500.00"), account.getBalance());
    }

    @Test
    void withdraw_Success() {
        User user = User.builder().email("test@example.com").build();

        Account account = Account.builder()
                .accountNumber("111111111111")
                .type(Account.AccountType.CHECKING)
                .balance(new BigDecimal("1000.00"))
                .user(user)
                .build();

        when(accountService.findByAccountNumber("111111111111")).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction result = transactionService.withdraw("111111111111", new BigDecimal("300.00"), "Withdrawal");

        assertNotNull(result);
        assertEquals(Transaction.TransactionType.WITHDRAWAL, result.getType());
        assertEquals(new BigDecimal("300.00"), result.getAmount());
        assertEquals(new BigDecimal("700.00"), account.getBalance());
    }

    @Test
    void withdraw_InsufficientFunds() {
        User user = User.builder().email("test@example.com").build();

        Account account = Account.builder()
                .accountNumber("111111111111")
                .type(Account.AccountType.CHECKING)
                .balance(new BigDecimal("100.00"))
                .user(user)
                .build();

        when(accountService.findByAccountNumber("111111111111")).thenReturn(account);

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.withdraw("111111111111", new BigDecimal("500.00"), "Withdrawal")
        );
    }
}