package com.sashimi.subscription.domain.repository;

import com.sashimi.subscription.domain.model.Subscription;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SubscriptionRepository {

    Subscription save(Subscription subscription);

    Optional<Subscription> findById(Long subscriptionId);

    Optional<Subscription> findActiveByUserId(
            Long userId,
            LocalDateTime now
    );
}