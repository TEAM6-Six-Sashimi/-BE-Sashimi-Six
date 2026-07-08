package com.sashimi.credit.application.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminCreditQueryUseCase {

    AdminCreditChargeHistory getCreditChargeHistory(
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            int page,
            int size
    );

    record AdminCreditChargeHistory(
            List<AdminCreditChargeHistoryItem> items,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }

    record AdminCreditChargeHistoryItem(
            int rowNumber,
            String orderNo,
            String loginId,
            Long chargedCredit,
            String paymentMethod,
            Long paidAmount,
            LocalDateTime approvedAt
    ) {
    }
}