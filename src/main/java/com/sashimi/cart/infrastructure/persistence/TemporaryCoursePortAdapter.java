package com.sashimi.cart.infrastructure.persistence;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.application.port.CoursePort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TemporaryCoursePortAdapter implements CoursePort {

    private final JdbcTemplate jdbcTemplate;

    public TemporaryCoursePortAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CourseInfo getCourseInfo(Long courseId) {
        String sql = """
                SELECT
                    c.course_id,
                    c.title,
                    c.price,
                    c.thumbnail,
                    u.name AS instructor_name,
                    c.status
                FROM courses c
                JOIN users u ON c.instructor_id = u.user_id
                WHERE c.course_id = ?
                """;

        List<CourseInfo> result = jdbcTemplate.query(sql, (rs, rowNum) -> new CourseInfo(
                rs.getLong("course_id"),
                rs.getString("title"),
                rs.getBigDecimal("price"),
                rs.getString("thumbnail"),
                rs.getString("instructor_name"),
                "APPROVED".equals(rs.getString("status"))
        ), courseId);

        if (result.isEmpty()) {
            throw new IllegalArgumentException("강의를 찾을 수 없습니다.");
        }

        return result.get(0);
    }
}