package com.sashimi.subscription.domain.repository;

import com.sashimi.subscription.domain.model.Subscription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {

    Subscription save(Subscription subscription);

    Optional<Subscription> findById(Long subscriptionId);

    Optional<Subscription> findActiveByUserId(Long userId, LocalDateTime now);

    Optional<Subscription> findActiveByUserIdForUpdate(Long userId);

    Optional<Subscription> findByIdForUpdate(Long subscriptionId);

    List<Long> findRenewalDueIds(LocalDateTime now);

    List<Long> findExpirationDueIds(LocalDateTime now);

}