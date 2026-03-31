package com.hemanth.Weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse implements Serializable {
    private String city;
    private String temperature;
    private String condition;
}