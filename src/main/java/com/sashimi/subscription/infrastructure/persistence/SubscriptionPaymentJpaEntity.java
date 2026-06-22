package com.sashimi.subscription.infrastructure.persistence;

import com.sashimi.subscription.domain.model.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_payments")
public class SubscriptionPaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_payment_id")
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_no", nullable = false)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_code", nullable = false)
    private SubscriptionPlan plan;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false)
    private SubscriptionBillingType billingType;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected SubscriptionPaymentJpaEntity() {
    }

    public static SubscriptionPaymentJpaEntity from(
            SubscriptionPayment payment
    ) {
        SubscriptionPaymentJpaEntity entity =
                new SubscriptionPaymentJpaEntity();

        entity.id = payment.getId();
        entity.subscriptionId = payment.getSubscriptionId();
        entity.orderId = payment.getOrderId();
        entity.paymentId = payment.getPaymentId();
        entity.userId = payment.getUserId();
        entity.orderNo = payment.getOrderNo();
        entity.plan = payment.getPlan();
        entity.amount = payment.getAmount();
        entity.billingType = payment.getBillingType();
        entity.paidAt = payment.getPaidAt();
        entity.createdAt = payment.getCreatedAt();

        return entity;
    }

    public SubscriptionPayment toDomain() {
        return SubscriptionPayment.restore(
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
}