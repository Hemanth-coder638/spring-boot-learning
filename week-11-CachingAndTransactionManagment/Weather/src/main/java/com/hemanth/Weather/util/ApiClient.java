package com.hemanth.Weather.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ApiClient {

    private final RestTemplate restTemplate=new RestTemplate();

    //private static final String API_KEY ="55a1619066964bfbac265836262703";

    public String fetchWeather(String city) {
        String url = "http://api.weatherapi.com/v1/current.json?key=55a1619066964bfbac265836262703&q="+city;

        log.info("🌐 Calling external Weather API for city: {}", city);

        return restTemplate.getForObject(url, String.class);
    }
}