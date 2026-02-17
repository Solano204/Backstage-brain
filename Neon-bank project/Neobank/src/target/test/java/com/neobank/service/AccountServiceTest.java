package com.neobank.service;

import neobank.dto.AccountCreateRequest;
import neobank.entity.Account;
import neobank.entity.User;
import neobank.exception.ResourceNotFoundException;
import neobank.repository.AccountRepository;
import neobank.service.AccountService;
import neobank.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_Success() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();

        AccountCreateRequest request = new AccountCreateRequest(Account.AccountType.CHECKING, "USD");

        when(userService.findById(1L)).thenReturn(user);
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        Account result = accountService.createAccount(1L, request);

        assertNotNull(result);
        assertEquals(Account.AccountType.CHECKING, result.getType());
        assertEquals("USD", result.getCurrency());
        assertEquals(user, result.getUser());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void findByAccountNumber_Success() {
        Account account = Account.builder()
                .accountNumber("123456789012")
                .type(Account.AccountType.CHECKING)
                .user(new User())
                .build();

        when(accountRepository.findByAccountNumber("123456789012")).thenReturn(Optional.of(account));

        Account result = accountService.findByAccountNumber("123456789012");

        assertNotNull(result);
        assertEquals("123456789012", result.getAccountNumber());
    }

    @Test
    void findByAccountNumber_NotFound() {
        when(accountRepository.findByAccountNumber("999999999999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                accountService.findByAccountNumber("999999999999")
        );
    }

    @Test
    void findUserAccounts_Success() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        Account account1 = Account.builder()
                .accountNumber("111111111111")
                .type(Account.AccountType.CHECKING)
                .user(user)
                .build();

        Account account2 = Account.builder()
                .accountNumber("222222222222")
                .type(Account.AccountType.SAVINGS)
                .user(user)
                .build();

        when(accountRepository.findByUserId(1L)).thenReturn(List.of(account1, account2));

        List<Account> results = accountService.findUserAccounts(1L);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(a -> a.getAccountNumber().equals("111111111111")));
        assertTrue(results.stream().anyMatch(a -> a.getAccountNumber().equals("222222222222")));
    }

    @Test
    void deactivateAccount_Success() {
        Account account = Account.builder()
                .id(1L)
                .accountNumber("123456789012")
                .type(Account.AccountType.CHECKING)
                .user(new User())
                .active(true)
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        accountService.deactivateAccount(1L);

        assertFalse(account.isActive());
        verify(accountRepository).save(account);
    }
}