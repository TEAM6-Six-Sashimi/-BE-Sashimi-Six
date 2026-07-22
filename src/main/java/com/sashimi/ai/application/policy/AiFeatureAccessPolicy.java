package com.sashimi.ai.application.policy;

import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.subscription.application.policy.ActiveSubscriptionPolicy;
import com.sashimi.user.domain.repository.UserRepository;
import org.springframework.stereotype.Component;
@Component
public class AiFeatureAccessPolicy {

    private final UserRepository userRepository;
    private final ActiveSubscriptionPolicy activeSubscriptionPolicy;
    private final AiMetrics aiMetrics;
    private final AiRateLimitPolicy aiRateLimitPolicy;

    public AiFeatureAccessPolicy(
            UserRepository userRepository,
            ActiveSubscriptionPolicy activeSubscriptionPolicy,
            AiMetrics aiMetrics,
            AiRateLimitPolicy aiRateLimitPolicy
    ) {
        this.userRepository = userRepository;
        this.activeSubscriptionPolicy = activeSubscriptionPolicy;
        this.aiMetrics = aiMetrics;
        this.aiRateLimitPolicy = aiRateLimitPolicy;
    }

    public void validate(Long userId, String feature) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        try {
            activeSubscriptionPolicy.validate(userId);
        } catch (BusinessException e) {
            aiMetrics.incrementRequestBlocked(
                    feature,
                    AiMetrics.REASON_SUBSCRIPTION_REQUIRED
            );
            throw e;
        }

        if (!user.isAiConsent()) {
            aiMetrics.incrementRequestBlocked(
                    feature,
                    AiMetrics.REASON_AI_CONSENT_REQUIRED
            );
            throw new BusinessException(ErrorCode.AI_CONSENT_REQUIRED);
        }

        try {
            aiRateLimitPolicy.validate(
                    userId,
                    AiFeatureType.fromMetricFeature(feature)
            );
        } catch (BusinessException e) {
            aiMetrics.incrementRequestBlocked(
                    feature,
                    AiMetrics.REASON_RATE_LIMIT_EXCEEDED
            );
            throw e;
        }
    }
}