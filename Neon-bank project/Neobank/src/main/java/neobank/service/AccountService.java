package neobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neobank.dto.AccountCreateRequest;
import neobank.entity.Account;
import neobank.entity.User;
import neobank.exception.*;
import neobank.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final Random random = new Random();

    @Transactional
    public Account createAccount(Long userId, AccountCreateRequest request) {
        log.info("Creating new account for user: {}", userId);

        User user = userService.findById(userId);
        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .type(request.type())
                .currency(request.currency())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return accountRepository.save(account);
    }

    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }

    public Account findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
    }

    public List<Account> findUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional
    public void deactivateAccount(Long accountId) {
        Account account = findById(accountId);
        account.setActive(false);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%012d", random.nextLong(1000000000000L));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}