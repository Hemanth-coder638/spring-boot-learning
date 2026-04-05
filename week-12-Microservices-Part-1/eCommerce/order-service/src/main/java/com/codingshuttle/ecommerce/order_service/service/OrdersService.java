package com.codingshuttle.ecommerce.order_service.service;

import com.codingshuttle.ecommerce.order_service.clients.InventoryOpenFeignClient;
import com.codingshuttle.ecommerce.order_service.dto.OrderRequestDto;
import com.codingshuttle.ecommerce.order_service.entity.OrderItem;
import com.codingshuttle.ecommerce.order_service.entity.OrderStatus;
import com.codingshuttle.ecommerce.order_service.entity.Orders;
import com.codingshuttle.ecommerce.order_service.repoitory.OrdersRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final ShippingCheckService shippingCheckService;
    private final OrdersRepository orderRepository;
    private final ModelMapper modelMapper;
    private final InventoryOpenFeignClient inventoryOpenFeignClient;

    public List<OrderRequestDto> getAllOrders() {
        log.info("Fetching all orders");
        List<Orders> orders = orderRepository.findAll();
        return orders.stream().map(order -> modelMapper.map(order, OrderRequestDto.class)).toList();
    }

    public OrderRequestDto getOrderById(Long id) {
        log.info("Fetching order with ID: {}", id);
        Orders order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return modelMapper.map(order, OrderRequestDto.class);
    }

//    @Retry(name = "inventoryRetry", fallbackMethod = "createOrderFallback")
    @CircuitBreaker(name = "inventoryCircuitBreaker", fallbackMethod = "createOrderFallback")
//    @RateLimiter(name = "inventoryRateLimiter", fallbackMethod = "createOrderFallback")
    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {
        log.info("Calling the createOrder method");
        Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequestDto);

        Orders orders = modelMapper.map(orderRequestDto, Orders.class);
        for(OrderItem orderItem: orders.getItems()) {
            orderItem.setOrder(orders);//Mapping Order Entity to OrderItem
        }
        orders.setTotalPrice(totalPrice);
        orders.setOrderStatus(OrderStatus.CONFIRMED);
        orders.setShippingWaitTime(LocalDateTime.now().plusMinutes(2));

        Orders savedOrder = orderRepository.save(orders);
        //checking shipping microservice server
        log.info(shippingCheckService.checkShippingService());
        return modelMapper.map(savedOrder, OrderRequestDto.class);
    }

    @Transactional
    public OrderRequestDto cancelOrder(Long id) {
        log.info("Cancelling order with id: {}", id);

        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(LocalDateTime.now().isAfter(order.getShippingWaitTime())){
            throw new RuntimeException("Order cannot be cancelled after shipping window");
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        OrderRequestDto orderRequestDto = modelMapper.map(order, OrderRequestDto.class);
        Double totalPrice=inventoryOpenFeignClient.restoreStocks(orderRequestDto);
        order.setOrderStatus(OrderStatus.CANCELLED);
        Orders savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderRequestDto.class);
    }

    public String shipOrder(Long id) {
        Orders order = orderRepository.findById(id).orElseThrow();
        if (!order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
            return "Order not eligible for shipping";
        }
        order.setOrderStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
        return "Order shipped";
    }
    public List<OrderRequestDto> getEligibleOrderForShipping(OrderStatus orderStatus,LocalDateTime localDateTime){
        List<Orders> orders=orderRepository.findByOrderStatusAndShippingWaitTimeLessThanEqual(orderStatus,localDateTime)
                .orElseThrow(()->new RuntimeException("No Eligible shipping orders found"));

        return orders.stream().map(order->modelMapper.map(order,OrderRequestDto.class)).toList();
    }

    public OrderRequestDto createOrderFallback(OrderRequestDto orderRequestDto, Throwable throwable) {
        log.error("Fallback occurred due to : {}", throwable.getMessage());

        return new OrderRequestDto();
    }

}










