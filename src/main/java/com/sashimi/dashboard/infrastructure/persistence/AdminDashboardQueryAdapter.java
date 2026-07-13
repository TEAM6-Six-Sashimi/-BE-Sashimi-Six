package com.sashimi.dashboard.infrastructure.persistence;

import com.sashimi.dashboard.application.port.AdminDashboardQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminDashboardQueryAdapter implements AdminDashboardQueryPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long sumCompletedCreditChargeAmount() {
        String sql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM credit_charge_payments
                WHERE status = 'DONE'
                """;

        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public Long sumPaidCourseSalesAmount() {
        String sql = """
                SELECT COALESCE(SUM(oi.final_price), 0)
                FROM payments p
                JOIN order_items oi ON oi.order_id = p.order_id
                WHERE p.status = 'PAID'
                  AND oi.item_type = 'COURSE'
                """;

        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public Long sumPaidSubscriptionAmount() {
        String sql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM subscription_payments
                """;

        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public long countActiveStudents() {
        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE status = 'ACTIVE'
                  AND role = 'STUDENT'
                """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count == null ? 0L : count;
    }

    @Override
    public long countActiveInstructors() {
        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE status = 'ACTIVE'
                  AND role = 'INSTRUCTOR'
                """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count == null ? 0L : count;
    }

    @Override
    public long countApprovedCourses() {
        String sql = """
                SELECT COUNT(*)
                FROM courses
                WHERE status = 'APPROVED'
                """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count == null ? 0L : count;
    }
}