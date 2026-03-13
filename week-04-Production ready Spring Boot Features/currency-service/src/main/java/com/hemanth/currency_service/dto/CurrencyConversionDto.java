package com.hemanth.currency_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConversionDto {
    private String fromCurrency;
    private String toCurrency;
    private double units;
    private double convertedValue;
}
