package com.sashimi.enrollment.application.service;

import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.enrollment.application.port.EnrollmentSummary;
import com.sashimi.enrollment.application.port.LearningProgressPort;
import com.sashimi.enrollment.application.usecase.LearningProgressCommandUseCase;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class LearningProgressCommandService implements LearningProgressCommandUseCase {

    /** 강의 구매일(수강 등록일) 기준 수강 가능 기간 (년) */
    private static final int ACCESS_PERIOD_YEARS = 2;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final EnrollmentPort enrollmentPort;
    private final LearningProgressPort learningProgressPort;

    @Override
    public ProgressResult reportProgress(Long userId, Long courseId, Long sessionId, int lastPositionSeconds) {
        EnrollmentSummary enrollment = enrollmentPort.getEnrollmentByCourse(userId, courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_FORBIDDEN));
        if (isAccessExpired(enrollment.enrolledAt())) {
            throw new BusinessException(ErrorCode.ENROLLMENT_EXPIRED);
        }

        int duration = learningProgressPort.getSessionDuration(courseId, sessionId);
        if (duration < 0) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        int position = Math.max(0, lastPositionSeconds);
        int watched = Math.min(position, duration);

        BigDecimal sessionRate = duration <= 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(watched).multiply(HUNDRED)
                        .divide(BigDecimal.valueOf(duration), 2, RoundingMode.DOWN);
        boolean sessionCompleted = duration > 0 && watched >= duration;

        learningProgressPort.upsertSessionProgress(
                userId, courseId, sessionId, position, watched, sessionRate, sessionCompleted);

        LearningProgressPort.CourseProgress course =
                learningProgressPort.recalculateAndUpdateEnrollment(userId, courseId);

        return new ProgressResult(sessionRate, sessionCompleted, course.progressRate(), course.completed());
    }

    private boolean isAccessExpired(LocalDateTime enrolledAt) {
        if (enrolledAt == null) {
            return false;
        }
        return enrolledAt.isBefore(LocalDateTime.now().minusYears(ACCESS_PERIOD_YEARS));
    }
}
