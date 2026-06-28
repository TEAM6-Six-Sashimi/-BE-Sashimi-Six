package com.sashimi.enrollment.application.port;

import java.math.BigDecimal;
import java.util.List;

public interface LearningProgressPort {

    /** 세션의 영상 길이(초) 조회. 해당 강의에 속한 세션이 아니면 -1 */
    int getSessionDuration(Long courseId, Long sessionId);

    /** 사용자의 강의 내 세션별 학습 진행 정보 목록 조회 */
    List<SessionProgressInfo> findSessionProgresses(Long userId, Long courseId);

    record SessionProgressInfo(Long sessionId, int lastPositionSeconds,
                               BigDecimal progressRate, boolean completed) {}

    /** 세션별 학습 진행 정보 갱신 (없으면 생성) */
    void upsertSessionProgress(Long userId, Long courseId, Long sessionId,
                               int lastPositionSeconds, int watchedSeconds,
                               BigDecimal progressRate, boolean completed);

    /** 강의 전체 진행률 재계산 후 enrollments 갱신 */
    CourseProgress recalculateAndUpdateEnrollment(Long userId, Long courseId);

    record CourseProgress(BigDecimal progressRate, boolean completed) {}
}
