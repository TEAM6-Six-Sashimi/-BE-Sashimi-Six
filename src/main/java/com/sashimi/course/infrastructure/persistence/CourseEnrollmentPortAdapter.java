package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.application.port.CourseEnrollmentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Map<Long, Integer> countCompletedByCourseIds(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = courseIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = """
                SELECT course_id, COUNT(*) AS completed_count
                FROM enrollments
                WHERE is_completed = true
                  AND course_id IN (%s)
                GROUP BY course_id
                """.formatted(placeholders);

        Map<Long, Integer> result = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getLong("course_id"), rs.getInt("completed_count"));
        }, courseIds.toArray());
        return result;
    }
}
