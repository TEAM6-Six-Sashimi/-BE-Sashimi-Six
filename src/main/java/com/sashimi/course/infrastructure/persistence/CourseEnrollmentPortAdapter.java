package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.application.port.CourseEnrollmentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CourseEnrollmentPortAdapter implements CourseEnrollmentPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean hasActiveEnrollment(Long courseId, LocalDateTime cutoff) {
        String sql = """
                SELECT COUNT(*)
                FROM enrollments
                WHERE course_id = ?
                  AND enrolled_at >= ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, courseId, cutoff);
        return count != null && count > 0;
    }
}
