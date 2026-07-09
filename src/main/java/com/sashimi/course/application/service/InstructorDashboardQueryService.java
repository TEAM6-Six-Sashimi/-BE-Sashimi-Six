package com.sashimi.course.application.service;

import com.sashimi.course.application.port.CourseEnrollmentPort;
import com.sashimi.course.application.port.InstructorCourseSales;
import com.sashimi.course.application.port.InstructorSalesQueryPort;
import com.sashimi.course.application.usecase.InstructorDashboardQueryUseCase;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
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
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InstructorDashboardQueryService implements InstructorDashboardQueryUseCase {

    private static final int PLATFORM_FEE_RATE = 30;
    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    /** 수강생이 존재할 수 있는 강의 상태 (승인 + 비공개) */
    private static final List<CourseStatus> STUDENT_VISIBLE_STATUSES =
            List.of(CourseStatus.APPROVED, CourseStatus.CLOSED);

    private final InstructorSalesQueryPort instructorSalesQueryPort;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentPort courseEnrollmentPort;

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

    @Override
    public InstructorStudentDashboard getStudentCounts(Long instructorId) {
        List<CourseStudentItem> courses = courseRepository
                .findByInstructorIdAndStatusIn(instructorId, STUDENT_VISIBLE_STATUSES)
                .stream()
                .map(this::toCourseStudentItem)
                .toList();

        int totalStudentCount = courses.stream()
                .mapToInt(CourseStudentItem::studentCount)
                .sum();

        return new InstructorStudentDashboard(totalStudentCount, courses);
    }

    private CourseStudentItem toCourseStudentItem(Course course) {
        return new CourseStudentItem(
                course.getId(),
                course.getTitle(),
                course.getStudentCount()
        );
    }

    @Override
    public InstructorCompletionDashboard getCompletionRates(Long instructorId) {
        List<Course> courses = courseRepository
                .findByInstructorIdAndStatusIn(instructorId, STUDENT_VISIBLE_STATUSES);

        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Integer> completedCounts = courseEnrollmentPort.countCompletedByCourseIds(courseIds);

        List<CourseCompletionItem> items = courses.stream()
                .map(course -> toCourseCompletionItem(course, completedCounts))
                .toList();

        return new InstructorCompletionDashboard(items);
    }

    private CourseCompletionItem toCourseCompletionItem(Course course, Map<Long, Integer> completedCounts) {
        int total = course.getStudentCount();
        int completed = completedCounts.getOrDefault(course.getId(), 0);
        int completionRate = total == 0 ? 0 : Math.round(completed * 100f / total);

        return new CourseCompletionItem(
                course.getId(),
                course.getTitle(),
                total,
                completed,
                completionRate
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