package com.sashimi.order.domain.repository;

import com.sashimi.order.domain.model.OrderItem;

import java.util.List;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    List<OrderItem> findAllByOrderId(Long orderId);
}