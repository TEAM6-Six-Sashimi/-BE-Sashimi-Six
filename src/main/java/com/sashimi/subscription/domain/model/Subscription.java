package com.sashimi.subscription.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.LocalDateTime;

public class Subscription {

    private final Long id;
    private final SubscriptionPlan plan;
    private final SubscriptionStatus status;
    private final Long price;
    private final LocalDateTime startedAt;
    private final LocalDateTime expiredAt;
    private final LocalDateTime nextBillingAt;
    private final boolean autoRenew;
    private final Long userId;

    private Subscription(
            Long id,
            SubscriptionPlan plan,
            SubscriptionStatus status,
            Long price,
            LocalDateTime startedAt,
            LocalDateTime expiredAt,
            LocalDateTime nextBillingAt,
            boolean autoRenew,
            Long userId
    ) {
        this.id = id;
        this.plan = plan;
        this.status = status;
        this.price = price;
        this.startedAt = startedAt;
        this.expiredAt = expiredAt;
        this.nextBillingAt = nextBillingAt;
        this.autoRenew = autoRenew;
        this.userId = userId;
    }

    public static Subscription start(
            Long userId,
            SubscriptionPlan plan,
            LocalDateTime startedAt
    ) {
        LocalDateTime expiredAt =
                plan.calculateExpiration(startedAt);

        return new Subscription(
                null,
                plan,
                SubscriptionStatus.ACTIVE,
                plan.getPrice(),
                startedAt,
                expiredAt,
                expiredAt,
                true,
                userId
        );
    }

    public static Subscription restore(
            Long id,
            SubscriptionPlan plan,
            SubscriptionStatus status,
            Long price,
            LocalDateTime startedAt,
            LocalDateTime expiredAt,
            LocalDateTime nextBillingAt,
            boolean autoRenew,
            Long userId
    ) {
        return new Subscription(
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

    public Subscription cancelRenewal() {
        if (status != SubscriptionStatus.ACTIVE) {
            throw new BusinessException(
                    ErrorCode.SUBSCRIPTION_NOT_ACTIVE
            );
        }

        if (!autoRenew) {
            throw new BusinessException(
                    ErrorCode.SUBSCRIPTION_ALREADY_CANCELLED
            );
        }

        return new Subscription(
                id,
                plan,
                status,
                price,
                startedAt,
                expiredAt,
                null,
                false,
                userId
        );
    }

    public Subscription renew(LocalDateTime renewedAt) {
        if (status != SubscriptionStatus.ACTIVE || !autoRenew) {
            throw new BusinessException(
                    ErrorCode.SUBSCRIPTION_RENEWAL_FAILED
            );
        }

        LocalDateTime newExpiredAt =
                plan.calculateExpiration(renewedAt);

        return new Subscription(
                id,
                plan,
                SubscriptionStatus.ACTIVE,
                price,
                startedAt,
                newExpiredAt,
                newExpiredAt,
                true,
                userId
        );
    }

    public Subscription expire() {
        return new Subscription(
                id,
                plan,
                SubscriptionStatus.EXPIRED,
                price,
                startedAt,
                expiredAt,
                null,
                false,
                userId
        );
    }

    public boolean isRenewalDue(LocalDateTime now) {
        return status == SubscriptionStatus.ACTIVE
                && autoRenew
                && nextBillingAt != null
                && !nextBillingAt.isAfter(now);
    }

    public boolean isExpirationDue(LocalDateTime now) {
        return status == SubscriptionStatus.ACTIVE
                && !autoRenew
                && expiredAt != null
                && !expiredAt.isAfter(now);
    }

    public boolean isActive(LocalDateTime now) {
        return status == SubscriptionStatus.ACTIVE
                && expiredAt != null
                && expiredAt.isAfter(now);
    }

    public Long getId() { return id; }
    public SubscriptionPlan getPlan() { return plan; }
    public SubscriptionStatus getStatus() { return status; }
    public Long getPrice() { return price; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public LocalDateTime getNextBillingAt() { return nextBillingAt; }
    public boolean isAutoRenew() { return autoRenew; }
    public Long getUserId() { return userId; }
}