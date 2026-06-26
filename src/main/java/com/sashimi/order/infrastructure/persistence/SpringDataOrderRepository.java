package com.sashimi.order.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, Long> {

    List<OrderJpaEntity> findAllByIdIn(List<Long> ids);
}