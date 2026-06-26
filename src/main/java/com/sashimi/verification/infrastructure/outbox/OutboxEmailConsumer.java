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

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void consume() {
        String idStr = redisTemplate.opsForList().rightPop(QUEUE_KEY);
        if (idStr == null) return;

        Long id = Long.parseLong(idStr);
        outboxRepository.findById(id).ifPresent(outbox -> {
            if (outbox.getStatus() != OutboxStatus.PENDING) return;

            try {
                emailSender.send(outbox.getToEmail(), outbox.getSubject(), outbox.getContent());
                outbox.markSent();
                outboxMetrics.recordSent();
                log.info("event=outbox_sent to={}", outbox.getToEmail());
            } catch (Exception e) {
                outbox.markFailed(MAX_RETRY);
                if (outbox.getRetryCount() >= MAX_RETRY) {
                    outboxMetrics.recordFailed();
                    log.error("event=outbox_exhausted to={}", outbox.getToEmail(), e);
                } else {
                    outboxMetrics.recordRetry();
                    redisTemplate.opsForList().leftPush(QUEUE_KEY, idStr);
                    log.warn("event=outbox_retry to={} retry={}", outbox.getToEmail(), outbox.getRetryCount());
                }
            }
            outboxRepository.save(outbox);
        });
    }
}
