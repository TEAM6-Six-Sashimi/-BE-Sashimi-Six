package com.sashimi.dashboard.application.service;

import com.sashimi.dashboard.application.port.InstructorDashboardQueryPort;
import com.sashimi.dashboard.application.usecase.InstructorDashboardQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InstructorDashboardQueryService implements InstructorDashboardQueryUseCase {

    private static final int PLATFORM_FEE_RATE = 30;
    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    private final InstructorDashboardQueryPort instructorDashboardQueryPort;

    @Override
    public InstructorDashboardSummary getMonthlySummary(
            Long instructorId,
            Integer year,
            Integer month
    ) {
        YearMonth targetMonth = resolveTargetMonth(year, month);
        LocalDateTime startAt = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endAt = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

        long totalSales = zeroIfNull(
                instructorDashboardQueryPort.sumMonthlyCourseSalesByInstructor(
                        instructorId,
                        startAt,
                        endAt
                )
        );

        long platformFee = totalSales * PLATFORM_FEE_RATE / 100;
        long settlementAmount = totalSales - platformFee;

        return new InstructorDashboardSummary(
                targetMonth.getYear(),
                targetMonth.getMonthValue(),
                totalSales,
                platformFee,
                settlementAmount,
                PLATFORM_FEE_RATE
        );
    }

    private YearMonth resolveTargetMonth(
            Integer year,
            Integer month
    ) {
        if (year == null || month == null) {
            return YearMonth.now(SERVICE_ZONE);
        }

        return YearMonth.of(year, month);
    }

    private long zeroIfNull(Long value) {
        return value == null ? 0L : value;
    }
}