package com.sashimi.verification.infrastructure.outbox;

import com.sashimi.verification.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEmailConsumer {

    private static final String QUEUE_KEY = "email_outbox_queue";
    private static final int MAX_RETRY = 3;

    private final StringRedisTemplate redisTemplate;
    private final SpringDataEmailOutboxRepository outboxRepository;
    private final EmailSender emailSender;
    private final OutboxMetrics outboxMetrics;

    @Transactional
    @Scheduled(fixedDelay = 500)
    public void consume() {
        String idStr = redisTemplate.opsForList().rightPop(QUEUE_KEY);
        if (idStr == null) return;

        Long id = Long.parseLong(idStr);
        int claimed = outboxRepository.claimForProcessing(id);
        if (claimed == 0) return;

        outboxRepository.findById(id).ifPresent(outbox -> {
            try {
                emailSender.send(outbox.getToEmail(), outbox.getSubject(), outbox.getContent());
                outbox.markSent();
                outboxMetrics.recordSent();
                log.info("event=outbox_sent outboxId={}", outbox.getId());
            } catch (Exception e) {
                outbox.markFailed(MAX_RETRY);
                if (outbox.getRetryCount() >= MAX_RETRY) {
                    outboxMetrics.recordFailed();
                    log.error("event=outbox_exhausted outboxId={}", outbox.getId(), e);
                } else {
                    outboxMetrics.recordRetry();
                    redisTemplate.opsForList().leftPush(QUEUE_KEY, idStr);
                    log.warn("event=outbox_retry outboxId={} retry={}", outbox.getId(), outbox.getRetryCount());
                }
            }
            outboxRepository.save(outbox);
        });
    }
}
