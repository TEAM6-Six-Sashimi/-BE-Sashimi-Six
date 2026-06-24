package com.sashimi.payment.infrastructure.persistence;

import com.sashimi.payment.domain.model.PaymentIdempotency;
import com.sashimi.payment.domain.repository.PaymentIdempotencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentIdempotencyRepositoryAdapter
        implements PaymentIdempotencyRepository {

    private final SpringDataPaymentIdempotencyRepository repository;

    @Override
    public PaymentIdempotency save(
            PaymentIdempotency idempotency
    ) {
        return repository.save(
                PaymentIdempotencyJpaEntity.from(idempotency)
        ).toDomain();
    }

    @Override
    public PaymentIdempotency saveAndFlush(
            PaymentIdempotency idempotency
    ) {
        return repository.saveAndFlush(
                PaymentIdempotencyJpaEntity.from(idempotency)
        ).toDomain();
    }


    @Override
    public Optional<PaymentIdempotency> findByIdForUpdate(
            Long id
    ) {
        return repository.findByIdForUpdate(id)
                .map(PaymentIdempotencyJpaEntity::toDomain);
    }

    @Override
    public Optional<PaymentIdempotency>
    findByUserIdAndIdempotencyKeyForUpdate(
            Long userId,
            String idempotencyKey
    ) {
        return repository
                .findByUserIdAndIdempotencyKeyForUpdate(
                        userId,
                        idempotencyKey
                )
                .map(PaymentIdempotencyJpaEntity::toDomain);
    }
}