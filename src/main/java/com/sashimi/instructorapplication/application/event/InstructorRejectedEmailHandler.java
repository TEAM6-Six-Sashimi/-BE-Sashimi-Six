package com.sashimi.instructorapplication.application.event;

import com.sashimi.verification.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstructorRejectedEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(InstructorRejectedEvent event) {
        try {
            emailSender.send(
                    event.email(),
                    "[FitGyeok] 강사 지원 결과 안내",
                    createContent(event.name(), event.rejectionReason())
            );
            log.info("강사 거절 이메일 발송 완료. userId={}", event.userId());
        } catch (RuntimeException e) {
            log.error("강사 거절 이메일 발송 실패. userId={}", event.userId(), e);
        }
    }

    private String createContent(String name, String rejectionReason) {
        String reason = (rejectionReason == null || rejectionReason.isBlank())
                ? "제출하신 서류 기준을 충족하지 못했습니다."
                : rejectionReason;

        return name + "님, 아쉽지만 이번 강사 지원은 승인되지 않았습니다.\n"
                + "사유: " + reason + "\n"
                + "서류를 보완하여 다시 지원하실 수 있습니다.";
    }
}
