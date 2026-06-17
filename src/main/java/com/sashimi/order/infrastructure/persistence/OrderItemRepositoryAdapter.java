package com.sashimi.order.infrastructure.persistence;

import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.order.domain.repository.OrderItemRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepositoryAdapter implements OrderItemRepository {

    private final SpringDataOrderItemRepository repository;

    public OrderItemRepositoryAdapter(SpringDataOrderItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        return repository.save(OrderItemJpaEntity.from(orderItem)).toDomain();
    }

    @Override
    public List<OrderItem> findAllByOrderId(Long orderId) {
        return repository.findAllByOrderId(orderId)
                .stream()
                .map(OrderItemJpaEntity::toDomain)
                .toList();
    }
}