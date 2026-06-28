package com.sashimi.course.application.port;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 강의 상세 조회 시 수강 여부·진행률을 enrollment 컨텍스트에서 가져오기 위한 포트.
 * 수강 가능 기간이 만료된 경우는 미수강으로 간주하여 empty 반환.
 */
public interface EnrollmentQueryPort {
    Optional<EnrollmentProgress> findActiveEnrollment(Long userId, Long courseId);

    /** 사용자의 강의 내 세션별 진행 정보 목록 (이어보기·세션별 진행률 표시용) */
    List<SessionProgress> findSessionProgresses(Long userId, Long courseId);

    record EnrollmentProgress(BigDecimal progressRate, boolean completed) {}

    record SessionProgress(Long sessionId, int lastPositionSeconds,
                           BigDecimal progressRate, boolean completed) {}
}
