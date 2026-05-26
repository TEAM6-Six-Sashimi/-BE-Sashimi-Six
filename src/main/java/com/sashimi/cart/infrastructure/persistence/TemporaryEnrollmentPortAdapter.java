package com.sashimi.cart.infrastructure.persistence;

import com.sashimi.cart.application.port.EnrollmentPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TemporaryEnrollmentPortAdapter implements EnrollmentPort {

    private final JdbcTemplate jdbcTemplate;

    public TemporaryEnrollmentPortAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
            (enrollment_type, progress_rate, is_completed, enrolled_at, completed_at, order_item_id, course_id, user_id)
            VALUES ('PAID', 0.00, false, CURRENT_TIMESTAMP, NULL, ?, ?, ?)
            """;

        try {
            jdbcTemplate.update(sql, orderItemId, courseId, userId);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }
    }
}