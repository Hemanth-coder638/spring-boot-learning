package com.hemanth.Banking.controller;

import com.hemanth.Banking.entity.Account;
import com.hemanth.Banking.service.LockingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/locking")
@RequiredArgsConstructor
public class LockingController {

    private final LockingService lockingService;

    @PutMapping("/optimistic/{accountNumber}")
    public Account optimisticLockUpdate(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        return lockingService.updateWithOptimisticLock(accountNumber, amount);
    }

    @PutMapping("/pessimistic/{accountNumber}")
    public Account pessimisticLockUpdate(@PathVariable String accountNumber,@RequestParam BigDecimal amount) {
        return lockingService.updateWithPessimisticLock(accountNumber, amount);
    }
}