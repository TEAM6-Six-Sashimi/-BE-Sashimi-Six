package com.sashimi.order.domain.repository;

import com.sashimi.order.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long orderId);

    List<Order> findAllByIdIn(List<Long> orderIds);
}