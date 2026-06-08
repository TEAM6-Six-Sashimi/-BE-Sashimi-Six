package com.sashimi.enrollment.infrastructure.persistence;

import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.enrollment.application.port.EnrollmentSummary;
import com.sashimi.enrollment.domain.model.EnrollmentType;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentPortAdapter implements EnrollmentPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isEnrolled(Long userId, Long courseId) {
        String sql = """
                SELECT COUNT(*)
                FROM enrollments
                WHERE user_id = ?
                  AND course_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userId,
                courseId
        );

        return count != null && count > 0;
    }

    @Override
    public void enrollPaidCourse(Long userId, Long courseId, Long orderItemId) {
        String sql = """
                INSERT INTO enrollments
                (
                    enrollment_type,
                    progress_rate,
                    is_completed,
                    enrolled_at,
                    completed_at,
                    order_item_id,
                    course_id,
                    user_id
                )
                VALUES
                (
                    ?,
                    0.00,
                    false,
                    CURRENT_TIMESTAMP,
                    NULL,
                    ?,
                    ?,
                    ?
                )
                """;

        try {
            jdbcTemplate.update(
                    sql,
                    EnrollmentType.PAID.name(),
                    orderItemId,
                    courseId,
                    userId
            );

            log.info("수강 등록 완료 - userId={}, courseId={}, orderItemId={}",
                    userId, courseId, orderItemId);

        } catch (DataIntegrityViolationException e) {
            log.warn("수강 등록 중복 요청 - userId={}, courseId={}, orderItemId={}",
                    userId, courseId, orderItemId);
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }
    }

    @Override
    public List<EnrollmentSummary> getEnrollmentsByUser(Long userId) {
        String sql = """
                SELECT course_id, progress_rate, is_completed, enrolled_at
                FROM enrollments
                WHERE user_id = ?
                ORDER BY enrolled_at DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new EnrollmentSummary(
                rs.getLong("course_id"),
                rs.getBigDecimal("progress_rate"),
                rs.getBoolean("is_completed"),
                rs.getObject("enrolled_at", LocalDateTime.class)
        ), userId);
    }

    @Override
    public Optional<EnrollmentSummary> getEnrollmentByCourse(Long userId, Long courseId) {
        String sql = """
                SELECT course_id, progress_rate, is_completed, enrolled_at
                FROM enrollments
                WHERE user_id = ? AND course_id = ?
                """;
        List<EnrollmentSummary> results = jdbcTemplate.query(sql, (rs, rowNum) -> new EnrollmentSummary(
                rs.getLong("course_id"),
                rs.getBigDecimal("progress_rate"),
                rs.getBoolean("is_completed"),
                rs.getObject("enrolled_at", LocalDateTime.class)
        ), userId, courseId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}