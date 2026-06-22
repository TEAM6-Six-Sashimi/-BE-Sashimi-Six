package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.application.port.ReviewCoursePort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
}
