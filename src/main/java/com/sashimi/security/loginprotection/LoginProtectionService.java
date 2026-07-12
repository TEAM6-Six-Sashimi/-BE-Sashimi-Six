package com.sashimi.security.loginprotection;

import com.sashimi.global.ratelimit.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 계정별 5분/5회 제한(RateLimiterService)만으로는 크리덴셜 스프레이(같은 IP가 서로 다른
 * 계정을 흔한 비밀번호로 훑는 공격)를 못 잡는다. 이 서비스는 (1) IP당 서로 다른 계정
 * 시도 개수로 스프레이를 탐지하고, (2) 같은 계정이 24시간 내 반복해서 걸리면 잠금 시간을
 * 5분 -> 30분 -> 1일로 늘린다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginProtectionService {

    private static final String IP_ATTEMPTS_PREFIX = "login-protect:ip-attempts:";
    private static final String IP_BLOCK_PREFIX = "login-protect:ip-block:";
    private static final String ACCOUNT_VIOLATIONS_PREFIX = "login-protect:account-violations:";
    private static final String ACCOUNT_LOCK_PREFIX = "login-protect:account-lock:";

    private static final int IP_ATTEMPTS_WINDOW_SECONDS = 300;
    private static final int IP_DISTINCT_ACCOUNT_THRESHOLD = 3;
    private static final int IP_BLOCK_SECONDS = 1800;

    private static final int ACCOUNT_FAIL_LIMIT = 5;
    private static final int ACCOUNT_FAIL_WINDOW_SECONDS = 300;
    private static final int ACCOUNT_VIOLATION_WINDOW_SECONDS = 86400;
    private static final long LOCK_SECONDS_VIOLATION_1 = 300;
    private static final long LOCK_SECONDS_VIOLATION_2 = 1800;
    private static final long LOCK_SECONDS_VIOLATION_3_PLUS = 86400;

    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimiterService rateLimiterService;

    public boolean isIpBlocked(String ip) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(IP_BLOCK_PREFIX + ip));
        } catch (Exception e) {
            // Redis 장애 시 fail-open (2차 방어선)
            log.warn("event=login_protection_redis_unavailable op=isIpBlocked");
            return false;
        }
    }

    public void recordFailedAttempt(String ip, String loginId) {
        try {
            String key = IP_ATTEMPTS_PREFIX + ip;
            Long setSize = redisTemplate.opsForSet().add(key, loginId);
            if (setSize != null && setSize == 1) {
                redisTemplate.expire(key, IP_ATTEMPTS_WINDOW_SECONDS, TimeUnit.SECONDS);
            }

            Long distinctCount = redisTemplate.opsForSet().size(key);
            if (distinctCount != null && distinctCount >= IP_DISTINCT_ACCOUNT_THRESHOLD) {
                redisTemplate.opsForValue().set(IP_BLOCK_PREFIX + ip, "1", IP_BLOCK_SECONDS, TimeUnit.SECONDS);
                log.warn("event=login_ip_blocked ip={} distinctAccounts={}", ip, distinctCount);
            }
        } catch (Exception e) {
            log.warn("event=login_protection_redis_unavailable op=recordFailedAttempt");
        }
    }

    public AccountLockCheckResult checkAccountLock(String loginId) {
        try {
            String lockKey = ACCOUNT_LOCK_PREFIX + loginId;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
                // 이미 처리된 크로싱: 카운터를 다시 건드리지 않고 곧장 잠김 응답
                return new AccountLockCheckResult(true, false, 0, 0);
            }

            boolean allowed = rateLimiterService.isAllowed(
                    "login:" + loginId, ACCOUNT_FAIL_LIMIT, ACCOUNT_FAIL_WINDOW_SECONDS);
            if (allowed) {
                return AccountLockCheckResult.notLocked();
            }

            // 방금 허용 한도를 처음 넘긴 순간 (크로싱)
            String violationKey = ACCOUNT_VIOLATIONS_PREFIX + loginId;
            Long violationCount = redisTemplate.opsForValue().increment(violationKey);
            redisTemplate.expire(violationKey, ACCOUNT_VIOLATION_WINDOW_SECONDS, TimeUnit.SECONDS);
            int violations = violationCount == null ? 1 : violationCount.intValue();

            long lockSeconds = resolveLockSeconds(violations);
            redisTemplate.opsForValue().set(lockKey, "1", lockSeconds, TimeUnit.SECONDS);

            boolean justEscalated = violations >= 2;
            log.warn("event=login_account_locked loginId={} violationCount={} lockSeconds={}",
                    maskLoginId(loginId), violations, lockSeconds);

            return new AccountLockCheckResult(true, justEscalated, violations, lockSeconds);
        } catch (Exception e) {
            log.warn("event=login_protection_redis_unavailable op=checkAccountLock");
            return AccountLockCheckResult.notLocked();
        }
    }

    private long resolveLockSeconds(int violations) {
        if (violations <= 1) return LOCK_SECONDS_VIOLATION_1;
        if (violations == 2) return LOCK_SECONDS_VIOLATION_2;
        return LOCK_SECONDS_VIOLATION_3_PLUS;
    }

    private String maskLoginId(String loginId) {
        if (loginId == null || loginId.length() <= 2) return "***";
        return loginId.charAt(0) + "*".repeat(loginId.length() - 2) + loginId.charAt(loginId.length() - 1);
    }
}
