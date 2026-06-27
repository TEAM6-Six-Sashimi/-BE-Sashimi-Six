package com.sashimi.payment.infrastructure.persistence;

import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentRepository repository;

    public PaymentRepositoryAdapter(
            SpringDataPaymentRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Payment save(Payment payment) {
        return repository.save(
                PaymentJpaEntity.from(payment)
        ).toDomain();
    }

    @Override
    public List<Payment> findAllByUserId(Long userId) {
        return repository
                .findAllByUserIdOrderByCreatedAtDescIdDesc(userId)
                .stream()
                .map(PaymentJpaEntity::toDomain)
                .toList();
    }
}