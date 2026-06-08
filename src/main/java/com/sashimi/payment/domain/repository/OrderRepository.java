package com.sashimi.payment.domain.repository;

import com.sashimi.payment.domain.model.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long orderId);
}