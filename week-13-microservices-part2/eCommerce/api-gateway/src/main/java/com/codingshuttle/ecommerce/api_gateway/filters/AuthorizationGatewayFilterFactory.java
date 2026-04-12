package com.codingshuttle.ecommerce.api_gateway.filters;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthorizationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AuthorizationGatewayFilterFactory.Config> {

    public AuthorizationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("AuthorizationGatewayFilter working");
        return (exchange, chain) -> {

            String rolesHeader = exchange.getRequest().getHeaders().getFirst("X-User-Roles");
            log.info("X_User-Roles="+rolesHeader);
            log.info("Allowed roles="+config.getAllowedRoles());
            if (rolesHeader == null || rolesHeader.isBlank()) {
                log.info("rolesHeader is empty error occured");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            Set<String> userRoles = Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            log.info(userRoles.toString());

            List<String> allowedRoles = config.getAllowedRoles();

            boolean allowed = userRoles.stream()
                    .anyMatch(allowedRoles::contains);
            log.info("roles allowed ="+allowed);

            if (!allowed) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    @Data
    public static class Config {
        private List<String> allowedRoles;
    }
}