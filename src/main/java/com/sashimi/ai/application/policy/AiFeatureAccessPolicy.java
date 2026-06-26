package com.sashimi.ai.application.policy;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.subscription.application.policy.ActiveSubscriptionPolicy;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class AiFeatureAccessPolicy {

    private final ActiveSubscriptionPolicy activeSubscriptionPolicy;
    private final UserRepository userRepository;

    public AiFeatureAccessPolicy(
            ActiveSubscriptionPolicy activeSubscriptionPolicy,
            UserRepository userRepository
    ) {
        this.activeSubscriptionPolicy = activeSubscriptionPolicy;
        this.userRepository = userRepository;
    }

    public void validate(Long userId) {
        activeSubscriptionPolicy.validate(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isAiConsent()) {
            throw new BusinessException(ErrorCode.AI_CONSENT_REQUIRED);
        }
    }
}