package com.hemanth.notification_service.kafka;

import com.hemanth.common.event.OrderCreatedEvent;
import com.hemanth.common.event.OrderStatusUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationConsumer {

    @KafkaListener(
            topics = "order_created",
            properties = {
                    "spring.json.value.default.type=com.hemanth.common.event.OrderCreatedEvent"
            }
    )
    public void listenOrderCreated(OrderCreatedEvent event) {
        log.info("NOTIFICATION → Order Created: {}", event);
    }

    @KafkaListener(
            topics = "order_status_updated",
            properties = {
                    "spring.json.value.default.type=com.hemanth.common.event.OrderStatusUpdatedEvent"
            }
    )
    public void listenOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        log.info("NOTIFICATION → Order Status Updated: {}", event);
    }
}