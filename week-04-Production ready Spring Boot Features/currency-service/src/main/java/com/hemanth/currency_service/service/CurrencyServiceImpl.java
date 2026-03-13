package com.hemanth.currency_service.service;

import com.hemanth.currency_service.dto.CurrencyConversionDto;
import com.hemanth.currency_service.exception.ResourceNotFoundException;
import com.hemanth.currency_service.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    @Value("${freecurrency.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.builder().build();

    @Override
    public CurrencyConversionDto convertCurrency(String from, String to, double units) {
        log.info("Starting currency conversion: {} -> {} for units {}", from, to, units);

        String url = "https://api.freecurrencyapi.com/v1/latest?apikey=" + apiKey + "&base_currency=" + from;

        log.debug("Calling external API: {}", url);

        Map<String, Object> response = restClient.get()
                .uri(url)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("data") == null) {
            throw new ResourceNotFoundException("Failed to get exchange rates from external API");
        }

        Map<String, Double> data = (Map<String, Double>) response.get("data");

        if (!data.containsKey(to)) {
            throw new ResourceNotFoundException("Currency not supported: " + to);
        }

        double rate = data.get(to);
        double finalValue = rate * units;

        log.info("Conversion success: 1 {} = {} {} | Final Value = {}", from, rate, to, finalValue);

        return CurrencyConversionDto.builder()
                .fromCurrency(from)
                .toCurrency(to)
                .units(units)
                .convertedValue(finalValue)
                .build();
    }
}
