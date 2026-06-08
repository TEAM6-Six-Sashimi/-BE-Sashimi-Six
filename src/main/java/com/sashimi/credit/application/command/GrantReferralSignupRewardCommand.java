package com.sashimi.credit.application.command;

public record GrantReferralSignupRewardCommand(
        Long newUserId,
        Long referrerUserId
) {
}