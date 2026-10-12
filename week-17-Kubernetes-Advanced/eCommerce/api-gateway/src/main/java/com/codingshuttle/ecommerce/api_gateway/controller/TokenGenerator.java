package com.codingshuttle.ecommerce.api_gateway.controller;

import com.codingshuttle.ecommerce.api_gateway.dto.TokenGenerateDto;
import com.codingshuttle.ecommerce.api_gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Slf4j
public class TokenGenerator {
    private final JwtService jwtService;
    public TokenGenerator(JwtService jwtService){
        this.jwtService=jwtService;
    }
    @PostMapping("/get-token")
    public String getToken(@RequestBody TokenGenerateDto tokenGenerateDto){
        String token= jwtService.generateToken(tokenGenerateDto);
        log.info(jwtService.getRolesFromToken(token).toString());
        return token;
    }
}
