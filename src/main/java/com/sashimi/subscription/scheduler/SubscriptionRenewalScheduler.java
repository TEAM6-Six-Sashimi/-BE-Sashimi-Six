package com.sashimi.subscription.scheduler;

import com.sashimi.subscription.application.service.SubscriptionRenewalProcessor;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class SubscriptionRenewalScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRenewalProcessor renewalProcessor;

    public SubscriptionRenewalScheduler(
            SubscriptionRepository subscriptionRepository,
            SubscriptionRenewalProcessor renewalProcessor
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.renewalProcessor = renewalProcessor;
    }

    @Scheduled(
            cron = "${subscription.renewal.cron:0 0 * * * *}"
    )
    public void processSubscriptions() {
        LocalDateTime now = LocalDateTime.now();

        processRenewals(now);
        processCancelledExpirations(now);
    }

    private void processRenewals(LocalDateTime now) {
        List<Long> ids =
                subscriptionRepository.findRenewalDueIds(now);

        for (Long id : ids) {
            try {
                SubscriptionRenewalProcessor.RenewalResult result =
                        renewalProcessor.renew(id, now);

                log.info(
                        "구독 자동 갱신 처리 - subscriptionId={}, result={}",
                        id,
                        result
                );
            } catch (Exception exception) {
                log.error(
                        "구독 자동 갱신 실패 - subscriptionId={}",
                        id,
                        exception
                );
            }
        }
    }

    private void processCancelledExpirations(
            LocalDateTime now
    ) {
        List<Long> ids =
                subscriptionRepository.findExpirationDueIds(now);

        for (Long id : ids) {
            try {
                renewalProcessor.expireCancelled(id, now);
            } catch (Exception exception) {
                log.error(
                        "해지 구독 만료 처리 실패 - subscriptionId={}",
                        id,
                        exception
                );
            }
        }
    }
}