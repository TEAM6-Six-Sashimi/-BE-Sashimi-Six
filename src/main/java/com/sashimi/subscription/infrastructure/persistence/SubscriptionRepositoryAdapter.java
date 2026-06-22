package com.sashimi.subscription.infrastructure.persistence;

import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.model.SubscriptionStatus;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class SubscriptionRepositoryAdapter
        implements SubscriptionRepository {

    private final SpringDataSubscriptionRepository repository;

    public SubscriptionRepositoryAdapter(
            SpringDataSubscriptionRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        return repository.save(
                SubscriptionJpaEntity.from(subscription)
        ).toDomain();
    }

    @Override
    public Optional<Subscription> findById(Long subscriptionId) {
        return repository.findById(subscriptionId)
                .map(SubscriptionJpaEntity::toDomain);
    }

    @Override
    public Optional<Subscription> findActiveByUserId(
            Long userId,
            LocalDateTime now
    ) {
        return repository
                .findByUserIdAndStatusAndExpiredAtAfterOrderByStartedAtDesc(
                        userId,
                        SubscriptionStatus.ACTIVE,
                        now,
                        PageRequest.of(0, 1)
                )
                .stream()
                .findFirst()
                .map(SubscriptionJpaEntity::toDomain);
    }
}