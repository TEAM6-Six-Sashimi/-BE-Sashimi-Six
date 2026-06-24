package com.sashimi.credit.application.usecase;

import com.sashimi.credit.application.result.CreditBalanceResult;

import java.time.LocalDateTime;
import java.util.List;

public interface CreditQueryUseCase {

    CreditBalanceResult getBalance(Long userId);

    CreditChargeHistory getChargeHistory(Long userId, int page, int size);

    record CreditChargeHistory(
            List<CreditChargeHistoryItem> items,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }

    record CreditChargeHistoryItem(
            Long creditChargePaymentId,
            String orderId,
            Long paidAmount,
            Long chargedCredit,
            LocalDateTime approvedAt,
            String paymentMethod
    ) {
    }
}