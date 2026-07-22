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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    public Set<Long> findEnrolledCourseIds(
            Long userId,
            List<Long> courseIds
    ) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Set.of();
        }

        List<Long> distinctCourseIds = courseIds.stream()
                .distinct()
                .toList();

        String placeholders = String.join(
                ", ",
                Collections.nCopies(
                        distinctCourseIds.size(),
                        "?"
                )
        );

        String sql = """
            SELECT course_id
            FROM enrollments
            WHERE user_id = ?
              AND course_id IN (%s)
            """.formatted(placeholders);

        Object[] parameters =
                new Object[distinctCourseIds.size() + 1];

        parameters[0] = userId;

        for (int index = 0;
             index < distinctCourseIds.size();
             index++) {
            parameters[index + 1] =
                    distinctCourseIds.get(index);
        }

        List<Long> enrolledCourseIds =
                jdbcTemplate.query(
                        sql,
                        (resultSet, rowNumber) ->
                                resultSet.getLong("course_id"),
                        parameters
                );

        return new HashSet<>(enrolledCourseIds);
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

            recalculateStudentCount(courseId);

            log.info("수강 등록 완료 - userId={}, courseId={}, orderItemId={}",
                    userId, courseId, orderItemId);

        } catch (DataIntegrityViolationException e) {
            log.warn("수강 등록 중복 요청 - userId={}, courseId={}, orderItemId={}",
                    userId, courseId, orderItemId);
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }
    }

    /**
     * courses.student_count 를 실제 enrollments 개수로 재집계한다.
     * 증감 방식이 아닌 재집계 방식이라 등록/취소와 무관하게 항상 정확하다.
     */
    private void recalculateStudentCount(Long courseId) {
        jdbcTemplate.update("""
                UPDATE courses
                SET student_count = (
                        SELECT COUNT(*)
                        FROM enrollments
                        WHERE course_id = ?
                    )
                WHERE course_id = ?
                """, courseId, courseId);
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