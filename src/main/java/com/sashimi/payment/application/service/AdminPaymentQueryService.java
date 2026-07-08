package com.sashimi.payment.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.application.usecase.AdminPaymentQueryUseCase;
import com.sashimi.payment.infrastructure.persistence.AdminPaymentQueryRepository;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPaymentQueryService implements AdminPaymentQueryUseCase {

    private static final int MAX_PAGE_SIZE = 100;

    private final AdminPaymentQueryRepository adminPaymentQueryRepository;

    @Override
    public AdminCoursePaymentHistory getCoursePaymentHistory(
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            int page,
            int size
    ) {
        validatePage(page, size);
        validateDateRange(startDate, endDate);

        LocalDateTime startAt = toStartAt(startDate);
        LocalDateTime endAt = toExclusiveEndAt(endDate);
        String normalizedKeyword = normalizeKeyword(keyword);

        AdminPaymentQueryRepository.PageResult<AdminPaymentQueryRepository.AdminCoursePaymentOrderRow> result =
                adminPaymentQueryRepository.findCoursePayments(
                        startAt,
                        endAt,
                        normalizedKeyword,
                        page,
                        size
                );

        List<Long> orderIds = result.items()
                .stream()
                .map(AdminPaymentQueryRepository.AdminCoursePaymentOrderRow::orderId)
                .toList();

        Map<Long, List<AdminCoursePaymentCourse>> courseMap =
                adminPaymentQueryRepository.findCourseItems(orderIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                AdminPaymentQueryRepository.AdminCoursePaymentCourseRow::orderId,
                                Collectors.mapping(
                                        item -> new AdminCoursePaymentCourse(
                                                item.courseTitle(),
                                                item.price()
                                        ),
                                        Collectors.toList()
                                )
                        ));

        List<AdminCoursePaymentHistoryItem> items = java.util.stream.IntStream.range(0, result.items().size())
                .mapToObj(index -> {
                    AdminPaymentQueryRepository.AdminCoursePaymentOrderRow row = result.items().get(index);
                    return new AdminCoursePaymentHistoryItem(
                            page * size + index + 1,
                            row.orderNo(),
                            row.userName(),
                            row.loginId(),
                            courseMap.getOrDefault(row.orderId(), List.of()),
                            row.totalAmount(),
                            row.paidAt()
                    );
                })
                .toList();

        return new AdminCoursePaymentHistory(
                items,
                result.totalElements(),
                calculateTotalPages(result.totalElements(), size),
                page,
                size
        );
    }

    @Override
    public AdminSubscriptionPaymentHistory getSubscriptionPaymentHistory(
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            String planCode,
            int page,
            int size
    ) {
        validatePage(page, size);
        validateDateRange(startDate, endDate);

        LocalDateTime startAt = toStartAt(startDate);
        LocalDateTime endAt = toExclusiveEndAt(endDate);
        String normalizedKeyword = normalizeKeyword(keyword);
        SubscriptionPlan plan = parsePlan(planCode);

        AdminPaymentQueryRepository.PageResult<AdminPaymentQueryRepository.AdminSubscriptionPaymentRow> result =
                adminPaymentQueryRepository.findSubscriptionPayments(
                        startAt,
                        endAt,
                        normalizedKeyword,
                        plan,
                        page,
                        size
                );

        List<AdminSubscriptionPaymentHistoryItem> items = java.util.stream.IntStream.range(0, result.items().size())
                .mapToObj(index -> {
                    AdminPaymentQueryRepository.AdminSubscriptionPaymentRow row = result.items().get(index);
                    return new AdminSubscriptionPaymentHistoryItem(
                            page * size + index + 1,
                            row.orderNo(),
                            row.userName(),
                            row.loginId(),
                            row.plan().name(),
                            row.plan().getPlanName(),
                            row.amount(),
                            row.paidAt()
                    );
                })
                .toList();

        return new AdminSubscriptionPaymentHistory(
                items,
                result.totalElements(),
                calculateTotalPages(result.totalElements(), size),
                page,
                size
        );
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private SubscriptionPlan parsePlan(String planCode) {
        if (planCode == null || planCode.isBlank()) {
            return null;
        }

        try {
            return SubscriptionPlan.valueOf(planCode.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim().toLowerCase();
    }

    private LocalDateTime toStartAt(LocalDate startDate) {
        if (startDate == null) {
            return null;
        }

        return startDate.atStartOfDay();
    }

    private LocalDateTime toExclusiveEndAt(LocalDate endDate) {
        if (endDate == null) {
            return null;
        }

        return endDate.plusDays(1).atStartOfDay();
    }

    private int calculateTotalPages(long totalElements, int size) {
        if (totalElements == 0) {
            return 0;
        }

        return (int) Math.ceil((double) totalElements / size);
    }
}
