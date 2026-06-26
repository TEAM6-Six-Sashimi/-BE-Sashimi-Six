package com.sashimi.enrollment.infrastructure.persistence;

import com.sashimi.course.application.port.EnrollmentQueryPort;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EnrollmentQueryPortAdapter implements EnrollmentQueryPort {

    /** 강의 구매일(수강 등록일) 기준 수강 가능 기간 (년) */
    private static final int ACCESS_PERIOD_YEARS = 2;

    private final EnrollmentPort enrollmentPort;

    @Override
    public Optional<EnrollmentProgress> findActiveEnrollment(Long userId, Long courseId) {
        return enrollmentPort.getEnrollmentByCourse(userId, courseId)
                .filter(summary -> !isAccessExpired(summary.enrolledAt()))
                .map(summary -> new EnrollmentProgress(summary.progressRate(), summary.completed()));
    }

    private boolean isAccessExpired(LocalDateTime enrolledAt) {
        if (enrolledAt == null) {
            return false;
        }
        return enrolledAt.isBefore(LocalDateTime.now().minusYears(ACCESS_PERIOD_YEARS));
    }
}
