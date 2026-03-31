package com.hemanth.Banking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {
    private String accountHolderName;
    private String accountNumber;
    private BigDecimal balance;
}