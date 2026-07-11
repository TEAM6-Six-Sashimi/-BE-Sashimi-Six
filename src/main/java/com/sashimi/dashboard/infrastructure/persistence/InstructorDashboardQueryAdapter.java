package com.sashimi.dashboard.infrastructure.persistence;

import com.sashimi.dashboard.application.port.InstructorDashboardQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class InstructorDashboardQueryAdapter implements InstructorDashboardQueryPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long sumMonthlyCourseSalesByInstructor(
            Long instructorId,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        String sql = """
                SELECT COALESCE(SUM(oi.final_price), 0)
                FROM payments p
                JOIN order_items oi ON oi.order_id = p.order_id
                JOIN courses c ON c.course_id = oi.course_id
                WHERE p.status = 'PAID'
                  AND oi.item_type = 'COURSE'
                  AND c.instructor_id = ?
                  AND p.paid_at >= ?
                  AND p.paid_at < ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                instructorId,
                startAt,
                endAt
        );
    }
}