package com.sashimi.order.infrastructure.persistence;

import com.sashimi.order.domain.model.Order;
import com.sashimi.order.domain.model.OrderStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @Column(name = "final_amount", nullable = false)
    private Long finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected OrderJpaEntity() {
    }

    private OrderJpaEntity(String orderNo, Long totalAmount, Long discountAmount,
                           Long finalAmount, OrderStatus status, LocalDateTime createdAt, Long userId) {
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public static OrderJpaEntity from(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
                order.getOrderNo(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getFinalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUserId()
        );
        entity.id = order.getId();
        return entity;
    }

    public Order toDomain() {
        return Order.restore(id, orderNo, totalAmount, discountAmount, finalAmount, status, createdAt, userId);
    }
}