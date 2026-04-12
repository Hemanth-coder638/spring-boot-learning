package com.hemanth.inventory_service.kafka;


import com.hemanth.common.event.OrderCreatedEvent;
import com.hemanth.inventory_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final ProductService productService;
    private final OrderStatusProducer orderStatusProducer;

    @KafkaListener(topics = "order_created", groupId = "inventory-service-group",containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String,OrderCreatedEvent> record) {
        OrderCreatedEvent event=record.value();

        Double total_price=0.0;
        log.info("Received order_created event: {}", event);

        try {
               total_price=productService.reduceStocksFromEvent(event);
               log.info("REDUCED STOCK PRICE="+total_price);
            orderStatusProducer.sendStatus(
                    event.getOrderId(),
                    "FULFILLED",
                    "Stock reduced successfully",
                    total_price
            );



        } catch (Exception e) {

            orderStatusProducer.sendStatus(
                    event.getOrderId(),
                    "OUT_OF_STOCK",
                    e.getMessage(),
                    total_price
            );
        }
    }
}