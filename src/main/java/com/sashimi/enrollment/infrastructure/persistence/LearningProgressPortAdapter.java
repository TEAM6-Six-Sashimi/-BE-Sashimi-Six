package com.sashimi.enrollment.infrastructure.persistence;

import com.sashimi.enrollment.application.port.LearningProgressPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LearningProgressPortAdapter implements LearningProgressPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int getSessionDuration(Long courseId, Long sessionId) {
        List<Integer> result = jdbcTemplate.query(
                "SELECT duration_seconds FROM course_sessions WHERE session_id = ? AND course_id = ?",
                (rs, rowNum) -> rs.getInt("duration_seconds"),
                sessionId, courseId
        );
        return result.isEmpty() ? -1 : result.get(0);
    }

    @Override
    public void upsertSessionProgress(Long userId, Long courseId, Long sessionId,
                                      int lastPositionSeconds, int watchedSeconds,
                                      BigDecimal progressRate, boolean completed) {
        jdbcTemplate.update("""
                INSERT INTO learning_progress
                    (user_id, course_id, session_id, watched_seconds, last_position_seconds,
                     progress_rate, is_completed, last_watched_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
                ON DUPLICATE KEY UPDATE
                    watched_seconds       = GREATEST(watched_seconds, ?),
                    last_position_seconds = ?,
                    progress_rate         = GREATEST(progress_rate, ?),
                    is_completed          = (is_completed OR ?),
                    last_watched_at       = NOW()
                """,
                userId, courseId, sessionId, watchedSeconds, lastPositionSeconds,
                progressRate, completed,
                watchedSeconds, lastPositionSeconds, progressRate, completed
        );
    }

    @Override
    public CourseProgress recalculateAndUpdateEnrollment(Long userId, Long courseId) {
        Integer totalSessions = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM course_sessions WHERE course_id = ?",
                Integer.class, courseId
        );

        BigDecimal sumRate = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(progress_rate), 0) FROM learning_progress WHERE user_id = ? AND course_id = ?",
                BigDecimal.class, userId, courseId
        );

        Integer completedCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM learning_progress WHERE user_id = ? AND course_id = ? AND is_completed = TRUE",
                Integer.class, userId, courseId
        );

        int total = totalSessions == null ? 0 : totalSessions;
        BigDecimal courseRate = total == 0
                ? BigDecimal.ZERO
                : sumRate.divide(BigDecimal.valueOf(total), 2, RoundingMode.DOWN);
        boolean courseCompleted = total > 0 && completedCount != null && completedCount == total;

        jdbcTemplate.update("""
                UPDATE enrollments
                SET progress_rate = ?,
                    is_completed  = ?,
                    completed_at  = CASE WHEN ? THEN COALESCE(completed_at, NOW()) ELSE NULL END
                WHERE user_id = ? AND course_id = ?
                """,
                courseRate, courseCompleted, courseCompleted, userId, courseId
        );

        return new CourseProgress(courseRate, courseCompleted);
    }
}
