package com.sashimi.maintenance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceModeService {

    private static final String ENABLED_KEY = "maintenance:enabled";
    private static final String MESSAGE_KEY = "maintenance:message";
    private static final String DEFAULT_MESSAGE = "현재 서비스 점검 중입니다. 잠시 후 다시 이용해 주세요.";
    private static final long CACHE_TTL_NANOS = TimeUnit.SECONDS.toNanos(3);

    private final StringRedisTemplate redisTemplate;

    private volatile boolean cachedEnabled = false;
    // System.currentTimeMillis()는 시스템 시간이 NTP 재동기화 등으로 과거로 튈 경우 캐시가
    // 영구 고착될 수 있어, 시계 조정에 영향받지 않는 nanoTime()으로 경과시간을 측정한다.
    private volatile long cachedAt = System.nanoTime() - CACHE_TTL_NANOS;

    public boolean isEnabled() {
        long now = System.nanoTime();
        if (now - cachedAt < CACHE_TTL_NANOS) {
            return cachedEnabled;
        }

        try {
            cachedEnabled = "true".equals(redisTemplate.opsForValue().get(ENABLED_KEY));
        } catch (Exception e) {
            // Redis 장애 시 fail-open: 점검모드 자체가 2차적인 접근 제어라 서비스 전체
            // 다운보다는 점검모드가 꺼진 것처럼 동작하는 편이 안전하다.
            log.warn("event=maintenance_redis_unavailable op=isEnabled");
            cachedEnabled = false;
        }
        cachedAt = now;
        return cachedEnabled;
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
        cachedEnabled = true;
        cachedAt = System.nanoTime();
        log.info("event=maintenance_mode_enabled message={}", message);
    }

    public void disable() {
        redisTemplate.opsForValue().set(ENABLED_KEY, "false");
        cachedEnabled = false;
        cachedAt = System.nanoTime();
        log.info("event=maintenance_mode_disabled");
    }
}
