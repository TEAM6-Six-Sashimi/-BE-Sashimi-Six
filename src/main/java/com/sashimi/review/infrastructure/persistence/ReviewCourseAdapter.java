package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.application.port.ReviewCoursePort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewCourseAdapter implements ReviewCoursePort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public String getCourseName(Long courseId) {
        return jdbcTemplate.queryForObject(
                "SELECT title FROM courses WHERE course_id = ?",
                String.class,
                courseId
        );
    }

    @Override
    public Map<Long, String> getCourseNamesBatch(List<Long> courseIds) {
        List<Long> distinctIds = courseIds.stream().distinct().toList();
        if (distinctIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = distinctIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT course_id, title FROM courses WHERE course_id IN (%s)".formatted(placeholders);

        Map<Long, String> namesById = new HashMap<>();
        jdbcTemplate.query(sql,
                rs -> { namesById.put(rs.getLong("course_id"), rs.getString("title")); },
                distinctIds.toArray());
        return namesById;
    }
}
