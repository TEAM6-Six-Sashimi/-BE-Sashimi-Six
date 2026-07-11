package com.sashimi.course.application.service;

import com.sashimi.course.application.port.CourseEnrollmentPort;
import com.sashimi.course.application.port.InstructorSalesQueryPort;
import com.sashimi.course.application.usecase.InstructorCourseStatisticsQueryUseCase;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InstructorCourseStatisticsQueryService implements InstructorCourseStatisticsQueryUseCase {

    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");
    private static final List<CourseStatus> STUDENT_VISIBLE_STATUSES =
            List.of(CourseStatus.APPROVED, CourseStatus.CLOSED);

    private final InstructorSalesQueryPort instructorSalesQueryPort;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentPort courseEnrollmentPort;

    @Override
    public InstructorCourseSalesStatistics getMonthlySales(
            Long instructorId,
            Integer year,
            Integer month
    ) {
        YearMonth targetMonth = resolveTargetMonth(year, month);
        LocalDateTime startAt = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endAt = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<CourseSalesItem> courses =
                instructorSalesQueryPort.findMonthlyCourseSales(
                                instructorId,
                                startAt,
                                endAt
                        )
                        .stream()
                        .map(item -> new CourseSalesItem(
                                item.courseId(),
                                item.courseTitle(),
                                item.salesAmount()
                        ))
                        .toList();

        long totalSales = courses.stream()
                .mapToLong(CourseSalesItem::salesAmount)
                .sum();

        return new InstructorCourseSalesStatistics(
                targetMonth.getYear(),
                targetMonth.getMonthValue(),
                totalSales,
                courses
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

    @Override
    public InstructorCourseStudentStatistics getStudentCounts(Long instructorId) {
        List<Course> courses = getVisibleInstructorCourses(instructorId);
        List<Long> courseIds = courses.stream()
                .map(Course::getId)
                .toList();

        Map<Long, Integer> studentCounts =
                courseEnrollmentPort.countStudentsByCourseIds(courseIds);

        List<CourseStudentItem> items = courses.stream()
                .map(course -> new CourseStudentItem(
                        course.getId(),
                        course.getTitle(),
                        studentCounts.getOrDefault(course.getId(), 0)
                ))
                .toList();

        int totalStudentCount = items.stream()
                .mapToInt(CourseStudentItem::studentCount)
                .sum();

        return new InstructorCourseStudentStatistics(totalStudentCount, items);
    }

    @Override
    public InstructorCourseCompletionStatistics getCompletionRates(Long instructorId) {
        List<Course> courses = getVisibleInstructorCourses(instructorId);
        List<Long> courseIds = courses.stream()
                .map(Course::getId)
                .toList();

        Map<Long, Integer> studentCounts =
                courseEnrollmentPort.countStudentsByCourseIds(courseIds);
        Map<Long, Integer> completedCounts =
                courseEnrollmentPort.countCompletedByCourseIds(courseIds);

        List<CourseCompletionItem> items = courses.stream()
                .map(course -> {
                    int totalStudentCount = studentCounts.getOrDefault(course.getId(), 0);
                    int completedStudentCount = completedCounts.getOrDefault(course.getId(), 0);
                    int completionRate = totalStudentCount <= 0
                            ? 0
                            : (int) Math.round((completedStudentCount * 100.0) / totalStudentCount);

                    return new CourseCompletionItem(
                            course.getId(),
                            course.getTitle(),
                            totalStudentCount,
                            completedStudentCount,
                            completionRate
                    );
                })
                .toList();

        return new InstructorCourseCompletionStatistics(items);
    }

    private List<Course> getVisibleInstructorCourses(Long instructorId) {
        return courseRepository.findByInstructorIdAndStatusIn(
                instructorId,
                STUDENT_VISIBLE_STATUSES
        );
    }
}
