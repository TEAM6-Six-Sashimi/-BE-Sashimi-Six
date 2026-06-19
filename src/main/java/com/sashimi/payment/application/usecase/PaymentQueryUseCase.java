package com.sashimi.payment.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentQueryUseCase {

    PaymentHistory getPaymentHistory(Long userId);
    PaymentPreview getCoursePreview(Long userId, Long courseId);
    PaymentPreview getCartPreview(Long userId);

    record PaymentHistory(List<PaymentHistoryItem> items) {
    }

    record PaymentHistoryItem(
            Long paymentId,
            Long orderId,
            String orderNo,
            Long amount,
            String paymentStatus,
            String orderStatus,
            LocalDateTime paidAt,
            LocalDateTime createdAt,
            List<PaymentHistoryCourse> courses
    ) { }
    record PaymentHistoryCourse(
            Long courseId,
            String title,
            Long price
    ) { }

    record PaymentPreview(
            String purchaseType,
            List<PaymentPreviewCourse> courses,
            Long totalAmount,
            Long creditBalance,
            Long balanceAfterPayment,
            Long insufficientAmount,
            boolean payable
    ) { }

    record PaymentPreviewCourse(
            Long courseId,
            String title,
            String thumbnail,
            String instructorName,
            Long price
    ) { }
}