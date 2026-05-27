package com.sashimi.resume.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class ResumeEvaluatedLogHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ResumeEvaluatedEvent event) {
        log.info(
                "Resume evaluated. userId={}, resumeId={}, evaluationId={}, jobPostingId={}, overallScore={}, evaluatedAt={}",
                event.userId(),
                event.resumeId(),
                event.evaluationId(),
                event.jobPostingId(),
                event.overallScore(),
                event.evaluatedAt()
        );
    }
}