package com.sashimi.payment.domain.model;

import java.time.LocalDateTime;

public class Payment {

    private final Long id;
    private final Long amount;
    private final PaymentStatus status;
    private final LocalDateTime paidAt;
    private final LocalDateTime createdAt;
    private final Long orderId;
    private final Long userId;

    private Payment(Long id, Long amount, PaymentStatus status, LocalDateTime paidAt,
                    LocalDateTime createdAt, Long orderId, Long userId) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.orderId = orderId;
        this.userId = userId;
    }

    public static Payment paid(Long amount, Long orderId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return new Payment(null, amount, PaymentStatus.PAID, now, now, orderId, userId);
    }

    public static Payment ready(Long amount, Long orderId, Long userId) {
        return new Payment(
                null,
                amount,
                PaymentStatus.READY,
                null,
                LocalDateTime.now(),
                orderId,
                userId
        );
    }

    public Payment markPaid() {
        return new Payment(
                id,
                amount,
                PaymentStatus.PAID,
                LocalDateTime.now(),
                createdAt,
                orderId,
                userId
        );
    }

    public Payment markFailed() {
        return new Payment(
                id,
                amount,
                PaymentStatus.FAILED,
                paidAt,
                createdAt,
                orderId,
                userId
        );
    }

    public Payment markCancelled() {
        return new Payment(
                id,
                amount,
                PaymentStatus.CANCELLED,
                paidAt,
                createdAt,
                orderId,
                userId
        );
    }

    public static Payment restore(Long id, Long amount, PaymentStatus status, LocalDateTime paidAt,
                                  LocalDateTime createdAt, Long orderId, Long userId) {
        return new Payment(id, amount, status, paidAt, createdAt, orderId, userId);
    }

    public Long getId() { return id; }
    public Long getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
}