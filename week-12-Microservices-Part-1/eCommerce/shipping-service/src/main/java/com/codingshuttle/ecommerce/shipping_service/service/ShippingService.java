package com.codingshuttle.ecommerce.shipping_service.service;

import com.codingshuttle.ecommerce.shipping_service.clients.ShippingOrderClient;
import com.codingshuttle.ecommerce.shipping_service.dto.OrderRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingService {

    @Autowired
    private ShippingOrderClient orderClient;

    // runs every 15 seconds
    @Scheduled(fixedRate = 15000)
    public void processShipping() {

        System.out.println("Checking for orders eligible for shipping...");

        List<OrderRequestDto> orders = orderClient.getEligibleOrders();

        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders ready for shipping.");
            return;
        }

        for (OrderRequestDto order : orders) {
            try {
                System.out.println("Shipping order ID: " + order.getId());

                orderClient.shipOrder(order.getId());

            } catch (Exception e) {
                System.out.println("Failed to ship order: " + order.getId());
            }
        }
    }
}