package com.hemanth.Banking.controller;


import com.hemanth.Banking.service.TransactionDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/demo/isolation")
@RequiredArgsConstructor
public class TransactionDemoController {

    private final TransactionDemoService transactionDemoService;

    @GetMapping("/read-committed/{accountNumber}")
    public BigDecimal readCommitted(@PathVariable String accountNumber) {
        return transactionDemoService.readBalanceWithReadCommitted(accountNumber);
    }

    @GetMapping("/repeatable-read/{accountNumber}")
    public BigDecimal repeatableRead(@PathVariable String accountNumber) {
        return transactionDemoService.readBalanceWithRepeatableRead(accountNumber);
    }

    @GetMapping("/serializable/{accountNumber}")
    public BigDecimal serializable(@PathVariable String accountNumber) {
        return transactionDemoService.readBalanceWithSerializable(accountNumber);
    }

    @PostMapping("/simulate-concurrent-updates/{accountNumber}")
    public String simulateConcurrentUpdates(@PathVariable String accountNumber) throws InterruptedException {
        return transactionDemoService.simulateConcurrentUpdate(accountNumber);
    }
}