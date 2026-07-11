package com.sashimi.credit.application.event;

import com.sashimi.verification.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditChargedEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CreditChargedEvent event) {
        try {
            emailSender.send(
                    event.email(),
                    "[FitGyeok] 크레딧 충전이 완료되었습니다",
                    createContent(event.name(), event.amount(), event.balanceAfter())
            );
            log.info("크레딧 충전 이메일 발송 완료. userId={}, amount={}", event.userId(), event.amount());
        } catch (RuntimeException e) {
            log.error("크레딧 충전 이메일 발송 실패. userId={}, amount={}", event.userId(), event.amount(), e);
        }
    }

    private String createContent(String name, Long amount, Long balanceAfter) {
        return name + "님, 크레딧 충전이 완료되었습니다.\n"
                + "충전 금액: " + amount + "\n"
                + "현재 잔액: " + balanceAfter;
    }
}
