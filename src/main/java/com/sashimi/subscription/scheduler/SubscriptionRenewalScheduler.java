package com.sashimi.subscription.scheduler;

import com.sashimi.subscription.application.service.SubscriptionRenewalProcessor;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class SubscriptionRenewalScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRenewalProcessor renewalProcessor;
    private final int batchSize;

    public SubscriptionRenewalScheduler(
            SubscriptionRepository subscriptionRepository,
            SubscriptionRenewalProcessor renewalProcessor,
            @Value("${subscription.renewal.batch-size:100}")
            int batchSize
    ) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "subscription renewal batch size must be positive"
            );
        }

        this.subscriptionRepository = subscriptionRepository;
        this.renewalProcessor = renewalProcessor;
        this.batchSize = batchSize;
    }

    @Scheduled(
            cron = "${subscription.renewal.cron:0 0 * * * *}"
    )
    public void processSubscriptions() {
        LocalDateTime now = LocalDateTime.now();

        log.info("구독 자동 갱신 배치 시작 - 기준시각={}", now);

        int renewalCount = processRenewals(now);
        int expirationCount = processCancelledExpirations(now);

        log.info("구독 자동 갱신 배치 종료 - 갱신조회건수={}, 만료조회건수={}", renewalCount, expirationCount);
    }

    private int processRenewals(LocalDateTime now) {long lastId = 0L;int processedCount = 0;
        while (true) {
            List<Long> ids = subscriptionRepository.findRenewalDueIdsAfter(now, lastId, batchSize);

            if (ids.isEmpty()) {break;
            }

            for (Long id : ids) {lastId = id;processedCount++;
                try {
                    SubscriptionRenewalProcessor.RenewalResult result =
                            renewalProcessor.renew(id, now);

                    log.debug("구독 자동 갱신 처리 - subscriptionId={}, result={}", id, result);
                } catch (Exception exception) {
                    log.error("구독 자동 갱신 실패 - subscriptionId={}", id, exception);
                }
            }

            if (ids.size() < batchSize) {
                break;
            }
        }

        return processedCount;
    }

    private int processCancelledExpirations(LocalDateTime now) {
        long lastId = 0L;
        int processedCount = 0;

        while (true) {
            List<Long> ids = subscriptionRepository.findExpirationDueIdsAfter(now, lastId, batchSize);

            if (ids.isEmpty()) {
                break;
            }

            for (Long id : ids) {
                lastId = id;
                processedCount++;

                try {
                    renewalProcessor.expireCancelled(id, now);
                } catch (Exception exception) {
                    log.error("해지 구독 만료 처리 실패 - subscriptionId={}", id, exception);
                }
            }

            if (ids.size() < batchSize) {
                break;
            }
        }

        return processedCount;
    }
}