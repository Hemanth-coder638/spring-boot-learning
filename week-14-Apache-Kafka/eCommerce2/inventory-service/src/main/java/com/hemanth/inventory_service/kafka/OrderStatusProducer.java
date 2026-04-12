package com.hemanth.inventory_service.kafka;


import com.hemanth.common.event.OrderStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusProducer {

    private final KafkaTemplate<String, OrderStatusUpdatedEvent> kafkaTemplate;

    public void sendStatus(Long orderId, String status, String message,Double total_price){

        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent();
        event.setOrderId(orderId);
        event.setStatus(status);
        event.setMessage(message);
        event.setTotalPrice(total_price);
        log.info("OrderStatusUpdatedEvent object sending to OrderService"+event);
        kafkaTemplate.send("order_status_updated", event);
    }
}