package com.sashimi.enrollment.application.service;

import com.sashimi.enrollment.application.port.CoursePort;
import com.sashimi.enrollment.application.port.EnrolledCourseInfo;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.enrollment.application.port.EnrollmentSummary;
import com.sashimi.enrollment.application.query.EnrolledCourseDetailView;
import com.sashimi.enrollment.application.query.EnrolledCourseView;
import com.sashimi.enrollment.application.usecase.StudentCourseQueryUseCase;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudentCourseQueryService implements StudentCourseQueryUseCase {

    /** 강의 구매일(수강 등록일) 기준 수강 가능 기간 (년) */
    private static final int ACCESS_PERIOD_YEARS = 2;

    private final EnrollmentPort enrollmentPort;
    private final CoursePort coursePort;

    @Override
    public List<EnrolledCourseView> getEnrolledCourses(Long userId) {
        return enrollmentPort.getEnrollmentsByUser(userId).stream()
                .filter(summary -> !isAccessExpired(summary.enrolledAt()))
                .map(summary -> {
                    EnrolledCourseInfo courseInfo = coursePort.getCourseInfo(summary.courseId());
                    return new EnrolledCourseView(
                            summary.courseId(),
                            courseInfo.title(),
                            courseInfo.thumbnail(),
                            courseInfo.instructorName(),
                            summary.progressRate(),
                            summary.completed()
                    );
                })
                .toList();
    }

    @Override
    public EnrolledCourseDetailView getEnrolledCourseDetail(Long userId, Long courseId) {
        EnrollmentSummary summary = enrollmentPort.getEnrollmentByCourse(userId, courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_FORBIDDEN));
        if (isAccessExpired(summary.enrolledAt())) {
            throw new BusinessException(ErrorCode.ENROLLMENT_EXPIRED);
        }
        return new EnrolledCourseDetailView(
                coursePort.getCourseDetailInfo(courseId),
                summary.progressRate(),
                summary.completed()
        );
    }

    /** 구매일로부터 수강 가능 기간이 지났는지 여부 */
    private boolean isAccessExpired(LocalDateTime enrolledAt) {
        if (enrolledAt == null) {
            return false;
        }
        return enrolledAt.isBefore(LocalDateTime.now().minusYears(ACCESS_PERIOD_YEARS));
    }
}