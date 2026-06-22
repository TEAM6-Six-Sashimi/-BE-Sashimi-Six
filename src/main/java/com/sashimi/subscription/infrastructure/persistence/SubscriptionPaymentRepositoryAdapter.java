package com.sashimi.subscription.infrastructure.persistence;

import com.sashimi.subscription.domain.model.SubscriptionPayment;
import com.sashimi.subscription.domain.repository.SubscriptionPaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionPaymentRepositoryAdapter
        implements SubscriptionPaymentRepository {

    private final SpringDataSubscriptionPaymentRepository repository;

    public SubscriptionPaymentRepositoryAdapter(
            SpringDataSubscriptionPaymentRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public SubscriptionPayment save(
            SubscriptionPayment subscriptionPayment
    ) {
        SubscriptionPaymentJpaEntity entity =
                SubscriptionPaymentJpaEntity.from(
                        subscriptionPayment
                );

        return repository.save(entity).toDomain();
    }

    @Override
    public PageResult findAllByUserId(
            Long userId,
            int page,
            int size
    ) {
        Page<SubscriptionPaymentJpaEntity> result =
                repository.findAllByUserIdOrderByPaidAtDesc(
                        userId,
                        PageRequest.of(page, size)
                );

        return new PageResult(
                result.getContent()
                        .stream()
                        .map(SubscriptionPaymentJpaEntity::toDomain)
                        .toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }
}