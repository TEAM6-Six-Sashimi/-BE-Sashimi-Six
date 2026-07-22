package com.sashimi.subscription.infrastructure.persistence;

import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SpringDataSubscriptionRepository repository;

    public SubscriptionRepositoryAdapter(SpringDataSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        return repository.save(SubscriptionJpaEntity.from(subscription)).toDomain();
    }

    @Override
    public Optional<Subscription> findById(Long subscriptionId) {
        return repository.findById(subscriptionId).map(SubscriptionJpaEntity::toDomain);
    }

    @Override
    public Optional<Subscription> findActiveByUserId(Long userId, LocalDateTime now) {
        return repository
                .findUsableByUserId(
                        userId,
                        now,
                        PageRequest.of(0, 1)
                )
                .stream()
                .findFirst()
                .map(SubscriptionJpaEntity::toDomain);
    }

    @Override
    public Optional<Subscription> findActiveByUserIdForUpdate(Long userId, LocalDateTime now) {
        return repository.findActiveByUserIdForUpdate(userId, now)
                .map(SubscriptionJpaEntity::toDomain);
    }

    @Override
    public Optional<Subscription> findByIdForUpdate(Long subscriptionId) {
        return repository.findByIdForUpdate(subscriptionId).map(SubscriptionJpaEntity::toDomain);
    }

    @Override
    public List<Long> findRenewalDueIdsAfter(LocalDateTime now, Long lastId, int limit) {
        return repository.findRenewalDueIdsAfter(now, lastId, PageRequest.of(0, limit));
    }

    @Override
    public List<Long> findExpirationDueIdsAfter(LocalDateTime now, Long lastId, int limit) {
        return repository.findExpirationDueIdsAfter(now, lastId, PageRequest.of(0, limit));
    }
}