package com.codingshuttle.ecommerce.order_service.service;

import com.codingshuttle.ecommerce.order_service.clients.ShippingOpenFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingCheckService {

    private final ShippingOpenFeignClient shippingClient;

    @Retry(name = "shippingRetry", fallbackMethod = "fallback")
    @CircuitBreaker(name = "shippingCB", fallbackMethod = "fallback")
    public String checkShippingService() {
        log.info("Checking shipping-service availability...");
        return shippingClient.pingShipping(); // will fail
    }

    public String fallback(Throwable t) {
        log.error("Shipping service is down: {}", t.getMessage());
        return "Shipping unavailable";
    }
}