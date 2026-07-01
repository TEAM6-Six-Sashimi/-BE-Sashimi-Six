package com.sashimi.verification.infrastructure.outbox;

import com.sashimi.verification.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEmailScheduler {

    private final SpringDataEmailOutboxRepository outboxRepository;
    private final EmailSender emailSender;
    private final OutboxMetrics outboxMetrics;

    private static final int MAX_RETRY = 3;

    // Redis Consumer가 즉시 처리; 이 스케줄러는 Redis 유실 시 fallback
    @Transactional
    @Scheduled(fixedDelay = 30000)
    public void processOutbox() {
        List<EmailOutboxJpaEntity> pending = outboxRepository.findByStatus(OutboxStatus.PENDING);

        if (pending.isEmpty()) return;

        log.info("event=outbox_processing count={}", pending.size());

        for (EmailOutboxJpaEntity outbox : pending) {
            int claimed = outboxRepository.claimForProcessing(outbox.getId());
            if (claimed == 0) continue;

            try {
                emailSender.send(outbox.getToEmail(), outbox.getSubject(), outbox.getContent());
                outbox.markSent();
                outboxMetrics.recordSent();
                log.info("event=outbox_sent outboxId={}", outbox.getId());
            } catch (Exception e) {
                outbox.markFailed(MAX_RETRY);
                if (outbox.getRetryCount() >= MAX_RETRY) {
                    outboxMetrics.recordFailed();
                    log.error("event=outbox_exhausted outboxId={} 최대 재시도 초과", outbox.getId(), e);
                } else {
                    outboxMetrics.recordRetry();
                    log.warn("event=outbox_retry_scheduled outboxId={} retry={}", outbox.getId(), outbox.getRetryCount(), e);
                }
            }
            outboxRepository.save(outbox);
        }
    }
}
