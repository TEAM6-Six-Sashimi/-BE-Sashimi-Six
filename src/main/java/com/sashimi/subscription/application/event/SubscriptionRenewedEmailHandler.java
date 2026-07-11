package com.sashimi.subscription.application.event;

import com.sashimi.verification.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionRenewedEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SubscriptionRenewedEvent event) {
        try {
            emailSender.send(
                    event.email(),
                    "[FitGyeok] 구독이 자동 갱신되었습니다",
                    createContent(event.name(), event.planName(), event.nextBillingAt())
            );
            log.info("구독 자동결제 이메일 발송 완료. userId={}", event.userId());
        } catch (RuntimeException e) {
            log.error("구독 자동결제 이메일 발송 실패. userId={}", event.userId(), e);
        }
    }

    private String createContent(String name, String planName, java.time.LocalDateTime nextBillingAt) {
        return name + "님, '" + planName + "' 구독이 정상적으로 자동 결제되었습니다.\n"
                + "다음 결제 예정일: " + nextBillingAt;
    }
}
