package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.application.port.ReviewUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewUserAdapter implements ReviewUserPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public String getUserLoginId(Long userId) {
        return jdbcTemplate.queryForObject(
                "SELECT login_id FROM users WHERE user_id = ?",
                String.class,
                userId
        );
    }
}
