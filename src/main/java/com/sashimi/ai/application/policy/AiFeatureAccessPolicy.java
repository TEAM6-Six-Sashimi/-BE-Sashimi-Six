package com.sashimi.ai.application.policy;

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

    public AiFeatureAccessPolicy(
            UserRepository userRepository,
            ActiveSubscriptionPolicy activeSubscriptionPolicy,
            AiMetrics aiMetrics
    ) {
        this.userRepository = userRepository;
        this.activeSubscriptionPolicy = activeSubscriptionPolicy;
        this.aiMetrics = aiMetrics;
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
    }
}