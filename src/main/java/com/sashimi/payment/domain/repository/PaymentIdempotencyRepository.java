package com.sashimi.payment.domain.repository;

import com.sashimi.payment.domain.model.PaymentIdempotency;

import java.util.Optional;

public interface PaymentIdempotencyRepository {

    PaymentIdempotency save(PaymentIdempotency idempotency);

    PaymentIdempotency saveAndFlush(PaymentIdempotency idempotency);

    Optional<PaymentIdempotency> findByUserIdAndIdempotencyKeyForUpdate(Long userId, String idempotencyKey);

    Optional<PaymentIdempotency> findByIdForUpdate(Long id);
}