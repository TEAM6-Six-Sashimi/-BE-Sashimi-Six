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

    /**
     * 카운터를 즉시 초기화한다. 예: 로그인 성공 시 실패 카운트를 정상 사용자에게
     * 불리하게 남겨두지 않기 위해 호출.
     */
    public void reset(String key) {
        try {
            redisTemplate.delete(PREFIX + key);
        } catch (Exception e) {
            log.warn("event=rate_limit_redis_unavailable op=reset key={}", key);
        }
    }
}
