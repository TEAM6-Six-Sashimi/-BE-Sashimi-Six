package com.sashimi.order.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

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

    private Order(
            Long id,
            String orderNo,
            Long totalAmount,
            Long discountAmount,
            Long finalAmount,
            OrderStatus status,
            LocalDateTime createdAt,
            Long userId
    ) {
        validate(
                orderNo,
                totalAmount,
                discountAmount,
                finalAmount,
                status,
                createdAt,
                userId
        );

        this.id = id;
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public static Order paid(
            String orderNo,
            Long totalAmount,
            Long userId
    ) {
        return new Order(
                null,
                orderNo,
                totalAmount,
                0L,
                totalAmount,
                OrderStatus.PAID,
                LocalDateTime.now(),
                userId
        );
    }

    public static Order pending(
            String orderNo,
            Long totalAmount,
            Long userId
    ) {
        return new Order(
                null,
                orderNo,
                totalAmount,
                0L,
                totalAmount,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                userId
        );
    }

    public Order markPaid() {
        if (status != OrderStatus.PENDING) {
            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATE_TRANSITION
            );
        }

        return new Order(
                id,
                orderNo,
                totalAmount,
                discountAmount,
                finalAmount,
                OrderStatus.PAID,
                createdAt,
                userId
        );
    }

    public Order markCancelled() {
        if (status != OrderStatus.PENDING
                && status != OrderStatus.PAID) {
            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATE_TRANSITION
            );
        }

        return new Order(
                id,
                orderNo,
                totalAmount,
                discountAmount,
                finalAmount,
                OrderStatus.CANCELLED,
                createdAt,
                userId
        );
    }

    public static Order restore(
            Long id,
            String orderNo,
            Long totalAmount,
            Long discountAmount,
            Long finalAmount,
            OrderStatus status,
            LocalDateTime createdAt,
            Long userId
    ) {
        return new Order(
                id,
                orderNo,
                totalAmount,
                discountAmount,
                finalAmount,
                status,
                createdAt,
                userId
        );
    }

    private void validate(
            String orderNo,
            Long totalAmount,
            Long discountAmount,
            Long finalAmount,
            OrderStatus status,
            LocalDateTime createdAt,
            Long userId
    ) {
        if (orderNo == null
                || orderNo.isBlank()
                || status == null
                || createdAt == null
                || userId == null
                || userId <= 0) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        if (totalAmount == null
                || discountAmount == null
                || finalAmount == null
                || totalAmount <= 0
                || discountAmount < 0
                || finalAmount <= 0
                || discountAmount > totalAmount
                || !finalAmount.equals(totalAmount - discountAmount)) {
            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_AMOUNT
            );
        }
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