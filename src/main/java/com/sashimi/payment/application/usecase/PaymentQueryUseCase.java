package com.sashimi.payment.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentQueryUseCase {

    PaymentHistory getPaymentHistory(Long userId);

    record PaymentHistory(List<PaymentHistoryItem> items) {
    }

    record PaymentHistoryItem(
            Long paymentId,
            Long orderId,
            String orderNo,
            BigDecimal amount,
            String paymentStatus,
            String orderStatus,
            LocalDateTime paidAt,
            LocalDateTime createdAt
    ) {
    }
}