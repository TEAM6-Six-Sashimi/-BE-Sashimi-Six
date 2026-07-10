package com.sashimi.credit.domain.repository;

import com.sashimi.credit.domain.model.CreditChargePayment;

import java.util.List;
import java.util.Optional;

public interface CreditChargePaymentRepository {

    CreditChargePayment save(CreditChargePayment payment);

    Optional<CreditChargePayment> findByOrderId(String orderId);

    Optional<CreditChargePayment> findByOrderIdForUpdate(String orderId);

    PageResult findCompletedByUserId(Long userId, int page, int size);

    List<CreditChargePayment> findNeedRetryTargets(int maxRetryCount, int limit);

    Optional<CreditChargePayment> findByIdForUpdate(Long id);

    record PageResult(List<CreditChargePayment> content, long totalElements, int totalPages, int page, int size) {
    }
}