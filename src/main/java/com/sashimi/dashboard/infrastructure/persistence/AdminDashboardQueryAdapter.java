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
}