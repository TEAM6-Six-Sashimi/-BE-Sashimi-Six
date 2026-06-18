package com.sashimi.credit.infrastructure.persistence;

import com.sashimi.credit.domain.model.CreditChargePayment;
import com.sashimi.credit.domain.repository.CreditChargePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CreditChargePaymentRepositoryAdapter implements CreditChargePaymentRepository {

    private final SpringDataCreditChargePaymentRepository springDataCreditChargePaymentRepository;

    @Override
    public CreditChargePayment save(CreditChargePayment payment) {
        return springDataCreditChargePaymentRepository.save(CreditChargePaymentJpaEntity.from(payment)).toDomain();
    }

    @Override
    public Optional<CreditChargePayment> findByOrderId(String orderId) {
        return springDataCreditChargePaymentRepository.findByOrderId(orderId)
                .map(CreditChargePaymentJpaEntity::toDomain);
    }

    @Override
    public Optional<CreditChargePayment> findByOrderIdForUpdate(String orderId) {
        return springDataCreditChargePaymentRepository.findByOrderIdForUpdate(orderId)
                .map(CreditChargePaymentJpaEntity::toDomain);
    }
}