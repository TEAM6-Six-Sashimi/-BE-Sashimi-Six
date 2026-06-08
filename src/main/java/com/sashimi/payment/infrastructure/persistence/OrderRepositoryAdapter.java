package com.sashimi.payment.infrastructure.persistence;

import com.sashimi.payment.domain.model.Order;
import com.sashimi.payment.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository repository;

    public OrderRepositoryAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return repository.save(OrderJpaEntity.from(order)).toDomain();
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return repository.findById(orderId).map(OrderJpaEntity::toDomain);
    }
}