package com.hemanth.currency_service.service;

import com.hemanth.currency_service.dto.CurrencyConversionDto;

public interface CurrencyService {
    CurrencyConversionDto convertCurrency(String from, String to, double units);
}
