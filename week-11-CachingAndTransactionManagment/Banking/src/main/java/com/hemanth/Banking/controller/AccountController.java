package com.hemanth.Banking.controller;

import com.hemanth.Banking.dto.AccountRequest;
import com.hemanth.Banking.dto.TransferRequest;
import com.hemanth.Banking.entity.Account;
import com.hemanth.Banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.hemanth.Banking.dto.MoneyRequest;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public Account createAccount(@RequestBody AccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{accountNumber}")
    public Account getAccount(@PathVariable String accountNumber) {
        return accountService.getAccount(accountNumber);
    }

    @PostMapping("/{accountNumber}/deposit")
    public Account deposit(@PathVariable String accountNumber, @RequestBody MoneyRequest request) {
        return accountService.deposit(accountNumber, request.getAmount());
    }

    @PostMapping("/{accountNumber}/withdraw")
    public Account withdraw(@PathVariable String accountNumber, @RequestBody MoneyRequest request) {
        return accountService.withdraw(accountNumber, request.getAmount());
    }

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request) {
        System.out.println("from"+request.getFromAccountNumber()+"to"+request.getToAccountNumber());
        accountService.transfer(request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount());
        return "Transfer completed successfully";
    }
}