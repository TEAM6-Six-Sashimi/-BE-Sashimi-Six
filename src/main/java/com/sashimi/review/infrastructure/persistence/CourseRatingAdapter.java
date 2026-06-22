package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.application.port.CourseRatingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseRatingAdapter implements CourseRatingPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void updateRating(Long courseId) {
        jdbcTemplate.update("""
                UPDATE courses
                SET rating_avg = (
                        SELECT COALESCE(AVG(rating), 0)
                        FROM reviews
                        WHERE course_id = ? AND status = 'ACTIVE'
                    ),
                    review_count = (
                        SELECT COUNT(*)
                        FROM reviews
                        WHERE course_id = ? AND status = 'ACTIVE'
                    )
                WHERE course_id = ?
                """, courseId, courseId, courseId);
    }
}
