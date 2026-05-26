package com.sashimi.payment.domain.repository;

import com.sashimi.payment.domain.model.OrderItem;

import java.util.List;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    List<OrderItem> findAllByOrderId(Long orderId);
}