package com.hemanth.Weather.controller;

import com.hemanth.Weather.dto.WeatherResponse;
import com.hemanth.Weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    // GET → CACHEABLE
    @GetMapping("/{city}")
    public WeatherResponse getWeather(@PathVariable String city) {
        return weatherService.getWeather(city);
    }

    // PUT → CACHE UPDATE
    @PutMapping("/{city}")
    public WeatherResponse updateWeather(@PathVariable String city) {
        return weatherService.updateWeather(city);
    }

    // DELETE → CACHE EVICT
    @DeleteMapping("/{city}")
    public String clearCache(@PathVariable String city) {
        return weatherService.clearCache(city);
    }
}