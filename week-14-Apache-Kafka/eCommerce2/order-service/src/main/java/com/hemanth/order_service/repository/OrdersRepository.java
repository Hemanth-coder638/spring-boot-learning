package com.hemanth.order_service.repository;

import com.hemanth.order_service.entity.OrderStatus;
import com.hemanth.order_service.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

}