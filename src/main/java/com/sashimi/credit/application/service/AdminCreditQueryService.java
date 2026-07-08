package com.sashimi.credit.application.service;

import com.sashimi.credit.application.usecase.AdminCreditQueryUseCase;
import com.sashimi.credit.infrastructure.persistence.AdminCreditChargeQueryRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCreditQueryService implements AdminCreditQueryUseCase {

    private static final int MAX_PAGE_SIZE = 100;

    private final AdminCreditChargeQueryRepository adminCreditChargeQueryRepository;

    @Override
    public AdminCreditChargeHistory getCreditChargeHistory(
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

        AdminCreditChargeQueryRepository.PageResult<AdminCreditChargeQueryRepository.AdminCreditChargePaymentRow> result =
                adminCreditChargeQueryRepository.findCreditChargePayments(
                        startAt,
                        endAt,
                        normalizedKeyword,
                        page,
                        size
                );

        List<AdminCreditChargeHistoryItem> items = IntStream.range(0, result.items().size())
                .mapToObj(index -> {
                    AdminCreditChargeQueryRepository.AdminCreditChargePaymentRow row =
                            result.items().get(index);

                    return new AdminCreditChargeHistoryItem(
                            page * size + index + 1,
                            row.orderNo(),
                            row.loginId(),
                            row.chargedCredit(),
                            row.paymentMethod(),
                            row.paidAmount(),
                            row.approvedAt()
                    );
                })
                .toList();

        return new AdminCreditChargeHistory(
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