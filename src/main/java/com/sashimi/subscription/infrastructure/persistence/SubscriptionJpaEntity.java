package com.sashimi.subscription.infrastructure.persistence;

import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import com.sashimi.subscription.domain.model.SubscriptionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
public class SubscriptionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "next_billing_at")
    private LocalDateTime nextBillingAt;

    @Column(name = "auto_renew", nullable = false)
    private boolean autoRenew;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected SubscriptionJpaEntity() {
    }

    public static SubscriptionJpaEntity from(
            Subscription subscription
    ) {
        SubscriptionJpaEntity entity =
                new SubscriptionJpaEntity();

        entity.id = subscription.getId();
        entity.plan = subscription.getPlan();
        entity.status = subscription.getStatus();
        entity.price = subscription.getPrice();
        entity.startedAt = subscription.getStartedAt();
        entity.expiredAt = subscription.getExpiredAt();
        entity.nextBillingAt = subscription.getNextBillingAt();
        entity.autoRenew = subscription.isAutoRenew();
        entity.userId = subscription.getUserId();

        return entity;
    }

    public Subscription toDomain() {
        return Subscription.restore(
                id,
                plan,
                status,
                price,
                startedAt,
                expiredAt,
                nextBillingAt,
                autoRenew,
                userId
        );
    }
}