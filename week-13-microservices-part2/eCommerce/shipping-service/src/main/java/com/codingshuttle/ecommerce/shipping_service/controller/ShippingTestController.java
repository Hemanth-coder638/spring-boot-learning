package com.codingshuttle.ecommerce.shipping_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ShippingTestController {

    @GetMapping("/test-shipping")
    public String pingShipping() {
        log.info("Ping received in shipping-service");
        return "Shipping service is running";
    }
}