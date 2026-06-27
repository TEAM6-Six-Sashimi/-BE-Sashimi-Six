package com.sashimi.order.infrastructure.persistence;

import com.sashimi.order.domain.model.OrderItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataOrderItemRepository extends JpaRepository<OrderItemJpaEntity, Long> {

    List<OrderItemJpaEntity> findAllByOrderId(Long orderId);

    List<OrderItemJpaEntity> findAllByOrderIdInAndItemType(
            List<Long> orderIds,
            OrderItemType itemType
    );
}