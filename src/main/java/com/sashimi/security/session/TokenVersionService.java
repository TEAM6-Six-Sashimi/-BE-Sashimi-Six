package com.sashimi.security.session;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenVersionService {

    private static final String VERSION_PREFIX = "token-version:";

    private final RedisTemplate<String, String> redisTemplate;

    public long incrementVersion(Long userId) {
        Long version = redisTemplate.opsForValue().increment(VERSION_PREFIX + userId);
        return version != null ? version : 1L;
    }

    public Long getVersion(Long userId) {
        String value = redisTemplate.opsForValue().get(VERSION_PREFIX + userId);
        if (value == null) return null;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isValidVersion(Long userId, Long tokenVersion) {
        if (userId == null || tokenVersion == null) return false;
        Long current = getVersion(userId);
        return tokenVersion.equals(current);
    }
}
