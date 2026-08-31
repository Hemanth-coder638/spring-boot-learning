package com.hemanth.Banking.service;

import com.hemanth.Banking.dto.AccountRequest;
import com.hemanth.Banking.entity.Account;
import com.hemanth.Banking.exception.AccountNotFoundException;
import com.hemanth.Banking.exception.InsufficientBalanceException;
import com.hemanth.Banking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Account createAccount(AccountRequest request) {
        Account account = Account.builder()
                .accountHolderName(request.getAccountHolderName())
                .accountNumber(request.getAccountNumber() != null ? request.getAccountNumber() : generateAccountNumber())
                .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
                .build();

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getAccount(String accountNumber) {
        return findAccountByAccountNumber(accountNumber);
    }

    @Transactional
    public Account deposit(String accountNumber, BigDecimal amount) {
        Account account = findAccountByAccountNumber(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        log.info("Deposited {} into account {}", amount, accountNumber);
        return account;
    }

    @Transactional
    public Account withdraw(String accountNumber, BigDecimal amount) {
        Account account = findAccountByAccountNumber(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        log.info("Withdrew {} from account {}", amount, accountNumber);
        return account;
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account fromAccount = findAccountByAccountNumber(fromAccountNumber);
        Account toAccount = findAccountByAccountNumber(toAccountNumber);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        log.info("Transferred {} from account {} to account {}", amount, fromAccountNumber, toAccountNumber);
    }

    private Account findAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with Account Number: " + accountNumber));
    }

    private String generateAccountNumber() {
        return "AC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}