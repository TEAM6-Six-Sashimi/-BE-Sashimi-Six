package com.sashimi.recommendation.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class JobPostingRecommendationAnalyzedLogHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(JobPostingRecommendationAnalyzedEvent event) {
        log.info(
                "Job posting recommendation analyzed. userId={}, recommendationId={}, jobRole={}, analyzedAt={}",
                event.userId(),
                event.recommendationId(),
                event.jobRole(),
                event.analyzedAt()
        );
    }
}