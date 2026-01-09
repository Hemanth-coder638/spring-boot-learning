package com.example.demo4.SecurityApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponseDto {

    private Long id;
    private String accessToken;
    private String refreshToken;
}
