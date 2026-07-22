package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.application.port.ReviewUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Map<Long, String> getUserLoginIdsBatch(List<Long> userIds) {
        List<Long> distinctIds = userIds.stream().distinct().toList();
        if (distinctIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = distinctIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT user_id, login_id FROM users WHERE user_id IN (%s)".formatted(placeholders);

        Map<Long, String> loginIdsById = new HashMap<>();
        jdbcTemplate.query(sql,
                rs -> { loginIdsById.put(rs.getLong("user_id"), rs.getString("login_id")); },
                distinctIds.toArray());
        return loginIdsById;
    }
}
