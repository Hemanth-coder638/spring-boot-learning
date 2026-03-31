package com.hemanth.Weather.service;

import com.hemanth.Weather.dto.WeatherResponse;
import com.hemanth.Weather.util.ApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;

    // CACHEABLE (MAIN LOGIC)
    @Cacheable(value = "weatherCache", key = "#city")
    public WeatherResponse getWeather(String city) {

        log.info("Cache MISS → Fetching from API for city: {}", city);

        try {
            String response = apiClient.fetchWeather(city);

            JsonNode root = objectMapper.readTree(response);

            String temp = root.path("current").path("temp_c").asText() + "°C";
            String condition = root.path("current").path("condition").path("text").asText();

            log.info("API Parsed → Temp: {}, Condition: {}", temp, condition);

            return new WeatherResponse(city, temp, condition);

        } catch (Exception e) {
            log.error("Error while parsing API response", e);
            throw new RuntimeException("Failed to fetch weather data");
        }
    }

    // FORCE UPDATE CACHE
    @CachePut(value = "weatherCache", key = "#city")
    public WeatherResponse updateWeather(String city) {

        log.info("Updating cache for city: {}", city);
        return getWeather(city); // reuse logic
    }

    // REMOVE CACHE
    @CacheEvict(value = "weatherCache", key = "#city")
    public String clearCache(String city) {

        log.info("🗑 Cache Evicted for city: {}", city);
        return "Cache cleared for " + city;
    }
}