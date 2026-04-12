package com.codingshuttle.ecommerce.order_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="shipping-service")
public interface ShippingOpenFeignClient {
    @GetMapping("/test-shipping")
    String pingShipping();
}
