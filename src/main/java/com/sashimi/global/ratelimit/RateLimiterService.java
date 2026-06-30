package com.sashimi.global.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private static final String PREFIX = "rate-limit:";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * @param key            rate limit 식별 키 (예: "login:userId")
     * @param maxRequests    windowSeconds 내 최대 허용 횟수
     * @param windowSeconds  제한 윈도우 (초)
     * @return true = 허용, false = 초과
     */
    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {
        try {
            String redisKey = PREFIX + key;
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count != null && count == 1) {
                redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
            }
            return count != null && count <= maxRequests;
        } catch (Exception e) {
            // Redis 장애 시 fail-open (rate limiting은 2차 방어선)
            log.warn("event=rate_limit_redis_unavailable key={}", key);
            return true;
        }
    }
}
