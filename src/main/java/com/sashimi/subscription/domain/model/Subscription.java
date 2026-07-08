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
    private final LocalDateTime gracePeriodUntil;
    private final LocalDateTime lastRenewalFailedAt;
    private final int renewalRetryCount;
    private final boolean autoRenew;
    private final Long userId;
    private static final long GRACE_PERIOD_DAYS = 7;

    private Subscription(
            Long id,
            SubscriptionPlan plan,
            SubscriptionStatus status,
            Long price,
            LocalDateTime startedAt,
            LocalDateTime expiredAt,
            LocalDateTime nextBillingAt,
            boolean autoRenew,
            Long userId,
            LocalDateTime gracePeriodUntil,
            LocalDateTime lastRenewalFailedAt,
            int renewalRetryCount
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
        this.gracePeriodUntil = gracePeriodUntil;
        this.lastRenewalFailedAt = lastRenewalFailedAt;
        this.renewalRetryCount = renewalRetryCount;
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
                userId,
                null,
                null,
                0
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
            Long userId,
            LocalDateTime gracePeriodUntil,
            LocalDateTime lastRenewalFailedAt,
            int renewalRetryCount
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
                userId,
                gracePeriodUntil,
                lastRenewalFailedAt,
                renewalRetryCount
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
                userId,
                gracePeriodUntil,
                lastRenewalFailedAt,
                renewalRetryCount
        );
    }

    public Subscription renew(LocalDateTime renewalBaseAt) {
        if ((status != SubscriptionStatus.ACTIVE && status != SubscriptionStatus.PAST_DUE)
                || !autoRenew
                || renewalBaseAt == null) {
            throw new BusinessException(ErrorCode.SUBSCRIPTION_RENEWAL_FAILED);
        }

        LocalDateTime newExpiredAt = plan.calculateExpiration(renewalBaseAt);

        return new Subscription(
                id,
                plan,
                SubscriptionStatus.ACTIVE,
                price,
                startedAt,
                newExpiredAt,
                newExpiredAt,
                true,
                userId,
                null,
                null,
                0
        );
    }

    public Subscription markPastDue(LocalDateTime failedAt) {
        if ((status != SubscriptionStatus.ACTIVE && status != SubscriptionStatus.PAST_DUE)
                || !autoRenew
                || nextBillingAt == null) {
            throw new BusinessException(ErrorCode.SUBSCRIPTION_RENEWAL_FAILED);
        }

        LocalDateTime graceUntil = gracePeriodUntil != null
                ? gracePeriodUntil
                : nextBillingAt.plusDays(GRACE_PERIOD_DAYS);

        return new Subscription(
                id,
                plan,
                SubscriptionStatus.PAST_DUE,
                price,
                startedAt,
                expiredAt,
                nextBillingAt,
                true,
                userId,
                graceUntil,
                failedAt,
                renewalRetryCount + 1
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
                userId,
                null,
                lastRenewalFailedAt,
                renewalRetryCount
        );
    }

    public boolean isRenewalDue(LocalDateTime now) {
        return (status == SubscriptionStatus.ACTIVE || status == SubscriptionStatus.PAST_DUE)
                && autoRenew
                && nextBillingAt != null
                && !nextBillingAt.isAfter(now);
    }

    public boolean isGracePeriodExpired(LocalDateTime now) {
        return status == SubscriptionStatus.PAST_DUE
                && gracePeriodUntil != null
                && !gracePeriodUntil.isAfter(now);
    }

    public boolean isExpirationDue(LocalDateTime now) {
        return status == SubscriptionStatus.ACTIVE
                && !autoRenew
                && expiredAt != null
                && !expiredAt.isAfter(now);
    }

    public boolean isActive(LocalDateTime now) {
        if (status == SubscriptionStatus.ACTIVE) {
            return expiredAt != null && expiredAt.isAfter(now);
        }

        if (status == SubscriptionStatus.PAST_DUE) {
            return gracePeriodUntil != null && gracePeriodUntil.isAfter(now);
        }

        return false;
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
    public LocalDateTime getGracePeriodUntil() {return gracePeriodUntil;}

    public LocalDateTime getLastRenewalFailedAt() {return lastRenewalFailedAt;}

    public int getRenewalRetryCount() {return renewalRetryCount;}
}