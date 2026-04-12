package com.codingshuttle.ecommerce.shipping_service.clients;

import com.codingshuttle.ecommerce.shipping_service.dto.OrderRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name= "order-service",path = "/orders")
public interface ShippingOrderClient {
    @GetMapping("/core/shipping-due")
    List<OrderRequestDto> getEligibleOrders();

    @PutMapping("/core/{id}/ship")
    void shipOrder(@PathVariable Long id);

}
