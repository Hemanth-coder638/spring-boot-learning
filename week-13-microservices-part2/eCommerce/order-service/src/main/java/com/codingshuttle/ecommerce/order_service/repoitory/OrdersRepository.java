package com.codingshuttle.ecommerce.order_service.repoitory;

import com.codingshuttle.ecommerce.order_service.entity.OrderStatus;
import com.codingshuttle.ecommerce.order_service.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Optional<List<Orders>> findByOrderStatusAndShippingWaitTimeLessThanEqual(OrderStatus orderStatus, LocalDateTime localDateTime);
}
