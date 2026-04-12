package com.hemanth.order_service.kafka;

import com.hemanth.common.event.OrderStatusUpdatedEvent;
import com.hemanth.order_service.entity.OrderStatus;
import com.hemanth.order_service.entity.Orders;
//import com.hemanth.order_service.event.OrderStatusUpdatedEvent;
import com.hemanth.order_service.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusUpdatedConsumer {

    private final OrdersRepository ordersRepository;

    @KafkaListener(topics = "order_status_updated", groupId = "order-service-group")
    public void consume(OrderStatusUpdatedEvent event) {
        log.info("OrderStatusUpdatedEvent RECEIVED FROM INVENTORY SERVICE="+event);

        Orders order = ordersRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(OrderStatus.valueOf(event.getStatus()));
        order.setTotalPrice(event.getTotalPrice());
        ordersRepository.save(order);

        log.info("Order {} updated to {}", event.getOrderId(), event.getStatus());
    }
}