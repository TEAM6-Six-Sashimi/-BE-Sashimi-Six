package com.sashimi.payment.domain.model;

import java.time.LocalDateTime;

public class Order {

    private final Long id;
    private final String orderNo;
    private final Long totalAmount;
    private final Long discountAmount;
    private final Long finalAmount;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final Long userId;

    private Order(Long id, String orderNo, Long totalAmount, Long discountAmount,
                  Long finalAmount, OrderStatus status, LocalDateTime createdAt, Long userId) {
        this.id = id;
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public static Order paid(String orderNo, Long totalAmount, Long userId) {
        return new Order(null, orderNo, totalAmount, 0L, totalAmount,
                OrderStatus.PAID, LocalDateTime.now(), userId);
    }

    public static Order restore(Long id, String orderNo, Long totalAmount, Long discountAmount,
                                Long finalAmount, OrderStatus status, LocalDateTime createdAt, Long userId) {
        return new Order(id, orderNo, totalAmount, discountAmount, finalAmount, status, createdAt, userId);
    }

    public Long getId() { return id; }
    public String getOrderNo() { return orderNo; }
    public Long getTotalAmount() { return totalAmount; }
    public Long getDiscountAmount() { return discountAmount; }
    public Long getFinalAmount() { return finalAmount; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getUserId() { return userId; }
}