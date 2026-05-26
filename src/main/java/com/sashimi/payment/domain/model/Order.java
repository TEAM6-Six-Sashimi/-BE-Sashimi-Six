package com.sashimi.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {

    private final Long id;
    private final String orderNo;
    private final BigDecimal totalAmount;
    private final BigDecimal discountAmount;
    private final BigDecimal finalAmount;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final Long userId;

    private Order(Long id, String orderNo, BigDecimal totalAmount, BigDecimal discountAmount,
                  BigDecimal finalAmount, OrderStatus status, LocalDateTime createdAt, Long userId) {
        this.id = id;
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public static Order paid(String orderNo, BigDecimal totalAmount, Long userId) {
        return new Order(null, orderNo, totalAmount, BigDecimal.ZERO, totalAmount,
                OrderStatus.PAID, LocalDateTime.now(), userId);
    }

    public static Order restore(Long id, String orderNo, BigDecimal totalAmount, BigDecimal discountAmount,
                                BigDecimal finalAmount, OrderStatus status, LocalDateTime createdAt, Long userId) {
        return new Order(id, orderNo, totalAmount, discountAmount, finalAmount, status, createdAt, userId);
    }

    public Long getId() { return id; }
    public String getOrderNo() { return orderNo; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getUserId() { return userId; }
}