package com.codingshuttle.ecommerce.order_service.dto;

import com.codingshuttle.ecommerce.order_service.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDto {
    private Long id;
    private List<OrderRequestItemDto> items;
    private OrderStatus orderStatus;
    private BigDecimal totalPrice;
}

