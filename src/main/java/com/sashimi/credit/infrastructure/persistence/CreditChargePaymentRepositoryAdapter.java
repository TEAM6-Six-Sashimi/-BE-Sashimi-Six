package com.sashimi.credit.infrastructure.persistence;

import com.sashimi.credit.domain.model.CreditChargePayment;
import com.sashimi.credit.domain.model.CreditChargePaymentStatus;
import com.sashimi.credit.domain.repository.CreditChargePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public PageResult findCompletedByUserId(Long userId, int page, int size) {
        Page<CreditChargePaymentJpaEntity> result =
                springDataCreditChargePaymentRepository
                        .findAllByUserIdAndStatusOrderByApprovedAtDescIdDesc(
                                userId,
                                CreditChargePaymentStatus.DONE,
                                PageRequest.of(page, size)
                        );

        return new PageResult(
                result.getContent()
                        .stream()
                        .map(CreditChargePaymentJpaEntity::toDomain)
                        .toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @Override
    public List<CreditChargePayment> findNeedRetryTargets(
            int maxRetryCount,
            int limit
    ) {
        return springDataCreditChargePaymentRepository
                .findRetryTargets(
                        CreditChargePaymentStatus.NEED_RETRY,
                        maxRetryCount,
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(CreditChargePaymentJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<CreditChargePayment> findByIdForUpdate(Long id) {
        return springDataCreditChargePaymentRepository.findByIdForUpdate(id)
                .map(CreditChargePaymentJpaEntity::toDomain);
    }
}
