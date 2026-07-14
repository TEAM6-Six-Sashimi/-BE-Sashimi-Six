package com.sashimi.maintenance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceModeService {

    private static final String ENABLED_KEY = "maintenance:enabled";
    private static final String MESSAGE_KEY = "maintenance:message";
    private static final String DEFAULT_MESSAGE = "현재 서비스 점검 중입니다. 잠시 후 다시 이용해 주세요.";

    private final StringRedisTemplate redisTemplate;

    public boolean isEnabled() {
        try {
            return "true".equals(redisTemplate.opsForValue().get(ENABLED_KEY));
        } catch (Exception e) {
            // Redis 장애 시 fail-open: 점검모드 자체가 2차적인 접근 제어라 서비스 전체
            // 다운보다는 점검모드가 꺼진 것처럼 동작하는 편이 안전하다.
            log.warn("event=maintenance_redis_unavailable op=isEnabled");
            return false;
        }
    }

    public String getMessage() {
        try {
            String message = redisTemplate.opsForValue().get(MESSAGE_KEY);
            return (message != null && !message.isBlank()) ? message : DEFAULT_MESSAGE;
        } catch (Exception e) {
            return DEFAULT_MESSAGE;
        }
    }

    public void enable(String message) {
        redisTemplate.opsForValue().set(ENABLED_KEY, "true");
        redisTemplate.opsForValue().set(MESSAGE_KEY, (message != null && !message.isBlank()) ? message : DEFAULT_MESSAGE);
        log.info("event=maintenance_mode_enabled message={}", message);
    }

    public void disable() {
        redisTemplate.opsForValue().set(ENABLED_KEY, "false");
        log.info("event=maintenance_mode_disabled");
    }
}
