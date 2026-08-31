package com.hemanth.Banking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyRequest {
    private BigDecimal amount;
}