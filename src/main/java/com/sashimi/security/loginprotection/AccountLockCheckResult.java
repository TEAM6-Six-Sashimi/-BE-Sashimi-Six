package com.sashimi.security.loginprotection;

public record AccountLockCheckResult(
        boolean locked,
        boolean justEscalated,
        int violationCount,
        long lockDurationSeconds
) {

    public static AccountLockCheckResult notLocked() {
        return new AccountLockCheckResult(false, false, 0, 0);
    }
}
