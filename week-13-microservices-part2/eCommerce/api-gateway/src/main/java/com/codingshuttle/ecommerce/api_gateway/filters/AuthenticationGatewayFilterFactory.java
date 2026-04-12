package com.codingshuttle.ecommerce.api_gateway.filters;

import com.codingshuttle.ecommerce.api_gateway.service.JwtService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final JwtService jwtService;

    public AuthenticationGatewayFilterFactory(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("AuthenticationGatewayFilter working");
        return (exchange, chain) -> {

//            if (!config.enabled) {
//                return chain.filter(exchange);
//            }

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                String token = authorizationHeader.substring(7);

                Long userId = jwtService.getUserIdFromToken(token);
                List<String> userRoles = jwtService.getRolesFromToken(token);
                log.info(userRoles.toString());

                return chain.filter(exchange.mutate().request(
                        exchange.getRequest()
                        .mutate()
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Roles", String.join(",", userRoles))
                        .build()
                        )
                        .build());

            } catch (Exception ex) {
                log.error("JWT validation failed: {}", ex.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    @Data
    public static class Config {
        private boolean enabled;
    }
}