package com.hemanth.order_service.service;


import com.hemanth.common.event.OrderCreatedEvent;
import com.hemanth.common.event.OrderCreatedItemEvent;
import com.hemanth.order_service.dto.OrderRequestDto;
import com.hemanth.order_service.entity.OrderItem;
import com.hemanth.order_service.entity.OrderStatus;
import com.hemanth.order_service.entity.Orders;
import com.hemanth.order_service.kafka.OrderEventProducer;
import com.hemanth.order_service.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final OrdersRepository orderRepository;
    private final ModelMapper modelMapper;
    private final OrderEventProducer orderEventProducer;

    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {

        Orders orders = modelMapper.map(orderRequestDto, Orders.class);

        for (OrderItem orderItem : orders.getItems()) {
            orderItem.setOrder(orders);
        }

        // Initial state
        orders.setOrderStatus(OrderStatus.PENDING);

        Orders savedOrder = orderRepository.save(orders);

        // Convert to event
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getId());

        List<OrderCreatedItemEvent> items = savedOrder.getItems().stream().map(item -> {
            OrderCreatedItemEvent e = new OrderCreatedItemEvent();
            e.setProductId(item.getProductId());
            e.setQuantity(item.getQuantity());
            return e;
        }).toList();

        event.setItems(items);
        event.setTotalPrice(savedOrder.getTotalPrice());

        // Send event to Kafka
        orderEventProducer.sendOrderCreatedEvent(event);

        return modelMapper.map(savedOrder, OrderRequestDto.class);
    }

}
