package com.sashimi.payment.application.event;

import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaidCourse;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;
import com.sashimi.verification.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentCompletedEvent event) {
        try {
            PaymentResult result = event.result();
            String subject = result.purchaseType() == PaymentPurchaseType.AI_SUBSCRIPTION
                    ? "[FitGyeok] 구독 결제가 완료되었습니다"
                    : "[FitGyeok] 강의 구매가 완료되었습니다";

            emailSender.send(event.email(), subject, createContent(event.name(), result));
            log.info("결제 완료 이메일 발송 완료. userId={}, orderId={}", event.userId(), result.orderId());
        } catch (RuntimeException e) {
            log.error("결제 완료 이메일 발송 실패. userId={}", event.userId(), e);
        }
    }

    private String createContent(String name, PaymentResult result) {
        if (result.purchaseType() == PaymentPurchaseType.AI_SUBSCRIPTION) {
            return name + "님, '" + result.subscription().planName() + "' 구독 결제가 완료되었습니다.\n"
                    + "결제 금액: " + result.amount() + "\n"
                    + "다음 결제일: " + result.subscription().nextBillingAt();
        }

        String courseTitles = result.courses().stream()
                .map(PaidCourse::title)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        return name + "님, 강의 구매가 완료되었습니다.\n"
                + "구매 강의: " + courseTitles + "\n"
                + "결제 금액: " + result.amount();
    }
}
