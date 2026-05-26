package com.sashimi.payment.infrastructure.persistence;

import com.sashimi.payment.domain.model.Order;
import com.sashimi.payment.domain.model.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
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
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected OrderJpaEntity() {
    }

    private OrderJpaEntity(String orderNo, BigDecimal totalAmount, BigDecimal discountAmount,
                           BigDecimal finalAmount, OrderStatus status, LocalDateTime createdAt, Long userId) {
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public static OrderJpaEntity from(Order order) {
        return new OrderJpaEntity(order.getOrderNo(), order.getTotalAmount(), order.getDiscountAmount(),
                order.getFinalAmount(), order.getStatus(), order.getCreatedAt(), order.getUserId());
    }

    public Order toDomain() {
        return Order.restore(id, orderNo, totalAmount, discountAmount, finalAmount, status, createdAt, userId);
    }
}