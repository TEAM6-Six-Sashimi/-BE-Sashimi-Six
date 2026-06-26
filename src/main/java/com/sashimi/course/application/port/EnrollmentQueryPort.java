package com.sashimi.course.application.port;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 강의 상세 조회 시 수강 여부·진행률을 enrollment 컨텍스트에서 가져오기 위한 포트.
 * 수강 가능 기간이 만료된 경우는 미수강으로 간주하여 empty 반환.
 */
public interface EnrollmentQueryPort {
    Optional<EnrollmentProgress> findActiveEnrollment(Long userId, Long courseId);

    record EnrollmentProgress(BigDecimal progressRate, boolean completed) {}
}
