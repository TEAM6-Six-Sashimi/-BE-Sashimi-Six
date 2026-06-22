package com.sashimi.subscription.domain.model;

import java.time.LocalDateTime;

public class SubscriptionPayment {

    private final Long id;
    private final Long subscriptionId;
    private final Long orderId;
    private final Long paymentId;
    private final Long userId;
    private final String orderNo;
    private final SubscriptionPlan plan;
    private final Long amount;
    private final SubscriptionBillingType billingType;
    private final LocalDateTime paidAt;
    private final LocalDateTime createdAt;

    private SubscriptionPayment(
            Long id,
            Long subscriptionId,
            Long orderId,
            Long paymentId,
            Long userId,
            String orderNo,
            SubscriptionPlan plan,
            Long amount,
            SubscriptionBillingType billingType,
            LocalDateTime paidAt,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.userId = userId;
        this.orderNo = orderNo;
        this.plan = plan;
        this.amount = amount;
        this.billingType = billingType;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    public static SubscriptionPayment initial(
            Long subscriptionId,
            Long orderId,
            Long paymentId,
            Long userId,
            String orderNo,
            SubscriptionPlan plan,
            Long amount,
            LocalDateTime paidAt
    ) {
        return new SubscriptionPayment(
                null,
                subscriptionId,
                orderId,
                paymentId,
                userId,
                orderNo,
                plan,
                amount,
                SubscriptionBillingType.INITIAL,
                paidAt,
                LocalDateTime.now()
        );
    }

    public static SubscriptionPayment restore(
            Long id,
            Long subscriptionId,
            Long orderId,
            Long paymentId,
            Long userId,
            String orderNo,
            SubscriptionPlan plan,
            Long amount,
            SubscriptionBillingType billingType,
            LocalDateTime paidAt,
            LocalDateTime createdAt
    ) {
        return new SubscriptionPayment(
                id,
                subscriptionId,
                orderId,
                paymentId,
                userId,
                orderNo,
                plan,
                amount,
                billingType,
                paidAt,
                createdAt
        );
    }

    public Long getId() { return id; }
    public Long getSubscriptionId() { return subscriptionId; }
    public Long getOrderId() { return orderId; }
    public Long getPaymentId() { return paymentId; }
    public Long getUserId() { return userId; }
    public String getOrderNo() { return orderNo; }
    public SubscriptionPlan getPlan() { return plan; }
    public Long getAmount() { return amount; }
    public SubscriptionBillingType getBillingType() { return billingType; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}