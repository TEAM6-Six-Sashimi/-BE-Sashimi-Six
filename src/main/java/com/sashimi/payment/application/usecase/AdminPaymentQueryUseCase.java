package com.sashimi.payment.application.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminPaymentQueryUseCase {
    AdminCoursePaymentHistory getCoursePaymentHistory(
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            int page,
            int size
    );

    AdminSubscriptionPaymentHistory getSubscriptionPaymentHistory(
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            String planCode,
            int page,
            int size
    );

    record AdminCoursePaymentHistory(
            List<AdminCoursePaymentHistoryItem> items,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }

    record AdminCoursePaymentHistoryItem(
            int rowNumber,
            String orderNo,
            String userName,
            String loginId,
            List<AdminCoursePaymentCourse> courses,
            Long totalAmount,
            LocalDateTime paidAt
    ) {
    }

    record AdminCoursePaymentCourse(
            String courseTitle,
            Long price
    ) {
    }

    record AdminSubscriptionPaymentHistory(
            List<AdminSubscriptionPaymentHistoryItem> items,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }

    record AdminSubscriptionPaymentHistoryItem(
            int rowNumber,
            String orderNo,
            String userName,
            String loginId,
            String planCode,
            String planName,
            Long amount,
            LocalDateTime paidAt
    ) {
    }
}
