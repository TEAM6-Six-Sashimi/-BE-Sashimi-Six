package com.sashimi.cart.infrastructure.persistence;

import com.sashimi.cart.application.port.EnrollmentPort;
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
}