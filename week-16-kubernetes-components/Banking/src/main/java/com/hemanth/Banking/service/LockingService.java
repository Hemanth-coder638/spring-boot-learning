package com.hemanth.Banking.service;


import com.hemanth.Banking.entity.Account;
import com.hemanth.Banking.exception.AccountNotFoundException;
import com.hemanth.Banking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockingService {

    private final AccountRepository accountRepository;

    @Transactional
    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2) // Wait 100ms, then 200ms
    )
    public Account updateWithOptimisticLock(String accountNumber, BigDecimal amount) {
        // 1. Fetch the latest account (and latest version) from the DB
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        // 2. Perform math
        account.setBalance(account.getBalance().add(amount));

        log.info("Attempting optimistic lock update for account {} (Version: {})",
                accountNumber, account.getVersion());

        // 3. Save triggers the version check
        // If another thread updated this meanwhile, Hibernate throws ObjectOptimisticLockingFailureException
        // and Spring Retry will catch it and restart this method from line 1.
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateWithPessimisticLock(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForPessimistic(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with account number: " + accountNumber));

        account.setBalance(account.getBalance().add(amount));
        log.info("Pessimistic lock update on account {}", accountNumber);

        return accountRepository.save(account);
    }
}