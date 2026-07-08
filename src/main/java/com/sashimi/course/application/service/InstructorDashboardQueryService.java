package com.sashimi.course.application.service;

import com.sashimi.course.application.port.InstructorCourseSales;
import com.sashimi.course.application.port.InstructorSalesQueryPort;
import com.sashimi.course.application.usecase.InstructorDashboardQueryUseCase;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InstructorDashboardQueryService implements InstructorDashboardQueryUseCase {

    private static final int PLATFORM_FEE_RATE = 30;
    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    private final InstructorSalesQueryPort instructorSalesQueryPort;

    @Override
    public InstructorSalesDashboard getMonthlySales(
            Long instructorId,
            Integer year,
            Integer month
    ) {
        YearMonth targetMonth = resolveTargetMonth(year, month);

        LocalDateTime startAt = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endAt = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<CourseSalesItem> courses = instructorSalesQueryPort
                .findMonthlyCourseSales(instructorId, startAt, endAt)
                .stream()
                .map(this::toCourseSalesItem)
                .toList();

        long totalSales = courses.stream()
                .mapToLong(CourseSalesItem::salesAmount)
                .sum();

        long platformFee = totalSales * PLATFORM_FEE_RATE / 100;
        long settlementAmount = totalSales - platformFee;

        return new InstructorSalesDashboard(
                targetMonth.getYear(),
                targetMonth.getMonthValue(),
                totalSales,
                platformFee,
                settlementAmount,
                PLATFORM_FEE_RATE,
                courses
        );
    }

    private YearMonth resolveTargetMonth(
            Integer year,
            Integer month
    ) {
        if (year == null && month == null) {
            return YearMonth.now(SERVICE_ZONE);
        }

        if (year == null || month == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private CourseSalesItem toCourseSalesItem(
            InstructorCourseSales sales
    ) {
        return new CourseSalesItem(
                sales.courseId(),
                sales.courseTitle(),
                sales.salesAmount()
        );
    }
}