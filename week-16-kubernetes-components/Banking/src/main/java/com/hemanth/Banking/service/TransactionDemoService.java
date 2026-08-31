package com.hemanth.Banking.service;


import com.hemanth.Banking.entity.Account;
import com.hemanth.Banking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionDemoService {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ExecutorService transactionExecutor;

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public BigDecimal readBalanceWithReadCommitted(String accountNumber) {
        Account account = findAccount(accountNumber);
        log.info("READ_COMMITTED - First read balance: {}", account.getBalance());
        sleep(5000);
        Account again = findAccount(accountNumber);
        log.info("READ_COMMITTED - Second read balance: {}", again.getBalance());
        return again.getBalance();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
    public BigDecimal readBalanceWithRepeatableRead(String accountNumber) {
        Account account = findAccount(accountNumber);
        log.info("REPEATABLE_READ - First read balance: {}", account.getBalance());
        sleep(5000);
        Account again = findAccount(accountNumber);
        log.info("REPEATABLE_READ - Second read balance: {}", again.getBalance());
        return again.getBalance();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, readOnly = true)
    public BigDecimal readBalanceWithSerializable(String accountNumber) {
        Account account = findAccount(accountNumber);
        log.info("SERIALIZABLE - First read balance: {}", account.getBalance());
        sleep(5000);
        Account again = findAccount(accountNumber);
        log.info("SERIALIZABLE - Second read balance: {}", again.getBalance());
        return again.getBalance();
    }

    public String simulateConcurrentUpdate(String accountNumber) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        transactionExecutor.submit(() -> {
            try {
                accountService.deposit(accountNumber, BigDecimal.valueOf(100));
            } finally {
                latch.countDown();
            }
        });

        transactionExecutor.submit(() -> {
            try {
                accountService.withdraw(accountNumber, BigDecimal.valueOf(50));
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        return "Concurrent update simulation completed for account " + accountNumber;
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountNumber));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted", e);
        }
    }
}