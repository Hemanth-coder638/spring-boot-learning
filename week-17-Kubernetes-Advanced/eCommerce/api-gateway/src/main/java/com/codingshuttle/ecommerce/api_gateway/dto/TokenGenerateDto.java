package com.codingshuttle.ecommerce.api_gateway.dto;

import lombok.Data;

import java.util.List;

@Data
public class TokenGenerateDto {
    Long userId;
    List<String> roles;
}
