package com.sashimi.order.infrastructure.persistence;

import com.sashimi.order.domain.model.Order;
import com.sashimi.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository repository;

    public OrderRepositoryAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return repository.save(OrderJpaEntity.from(order))
            .toDomain();
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return repository.findById(orderId)
                .map(OrderJpaEntity::toDomain);
    }

    @Override
    public List<Order> findAllByIdIn(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return List.of();
        }

        return repository.findAllByIdIn(orderIds)
                .stream()
                .map(OrderJpaEntity::toDomain)
                .toList();
    }
}