package com.sashimi.review.infrastructure.persistence;

import com.sashimi.course.application.port.CourseReviewPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseReviewPortAdapter implements CourseReviewPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ReviewInfo> findActiveReviewsByCourseId(Long courseId) {
        return jdbcTemplate.query(
                """
                SELECT r.review_id, r.rating, r.content, u.login_id, r.created_at
                FROM reviews r
                JOIN users u ON r.user_id = u.user_id
                WHERE r.course_id = ? AND r.status = 'ACTIVE'
                ORDER BY r.created_at DESC
                """,
                (rs, rowNum) -> new ReviewInfo(
                        rs.getLong("review_id"),
                        rs.getInt("rating"),
                        rs.getString("content"),
                        rs.getString("login_id"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ),
                courseId
        );
    }
}
