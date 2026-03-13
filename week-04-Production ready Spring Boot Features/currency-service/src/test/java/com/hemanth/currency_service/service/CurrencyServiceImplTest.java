package com.hemanth.currency_service.service;

import com.hemanth.currency_service.dto.CurrencyConversionDto;
import com.hemanth.currency_service.exception.ResourceNotFoundException;
import com.hemanth.currency_service.service.CurrencyServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceImplTest {

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private RestClient.Builder restClientBuilder;
    @Mock
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClientBuilder = mock(RestClient.Builder.class);
        restClient = mock(RestClient.class);

        // injecting RestClient instance into our service
        ReflectionTestUtils.setField(currencyService, "restClient", restClient);

        // injecting API key
        ReflectionTestUtils.setField(currencyService, "apiKey", "FAKE_API_KEY");
    }

    @Test
    void testConvertCurrency_Success() {

        Map<String, Object> mockResponse = Map.of(
                "data", Map.of("USD", 0.012)
        );

        var mockSpec = mock(RestClient.RequestHeadersUriSpec.class);
        var mockRetrieve = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(mockSpec);
        when(mockSpec.uri(anyString())).thenReturn(mockSpec);
        when(mockSpec.retrieve()).thenReturn(mockRetrieve);
        when(mockRetrieve.body(Map.class)).thenReturn(mockResponse);

        CurrencyConversionDto result =
                currencyService.convertCurrency("INR", "USD", 100);

        assertEquals(1.2, result.getConvertedValue());
        assertEquals("INR", result.getFromCurrency());
        assertEquals("USD", result.getToCurrency());
    }

    @Test
    void testConvertCurrency_InvalidCurrency_ShouldThrow() {

        Map<String, Object> mockResponse = Map.of(
                "data", Map.of("EUR", 0.011)
        );

        var mockSpec = mock(RestClient.RequestHeadersUriSpec.class);
        var mockRetrieve = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(mockSpec);
        when(mockSpec.uri(anyString())).thenReturn(mockSpec);
        when(mockSpec.retrieve()).thenReturn(mockRetrieve);
        when(mockRetrieve.body(Map.class)).thenReturn(mockResponse);

        assertThrows(ResourceNotFoundException.class, () ->
                currencyService.convertCurrency("INR", "USD", 100)
        );
    }
}
