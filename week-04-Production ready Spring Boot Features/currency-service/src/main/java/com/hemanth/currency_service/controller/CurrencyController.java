package com.hemanth.currency_service.controller;

import com.hemanth.currency_service.dto.CurrencyConversionDto;
import com.hemanth.currency_service.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/convert")
    public ResponseEntity<CurrencyConversionDto> convertCurrency(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam double units
    ) {
        CurrencyConversionDto dto =
                currencyService.convertCurrency(fromCurrency, toCurrency, units);
        return ResponseEntity.ok(dto);
    }
}
