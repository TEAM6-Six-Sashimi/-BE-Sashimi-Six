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
public class InstructorAppliedEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(InstructorAppliedEvent event) {
        try {
            emailSender.send(
                    event.email(),
                    "[FitGyeok] 강사 지원이 접수되었습니다",
                    createContent(event.name())
            );
            log.info("강사 지원 접수 이메일 발송 완료. userId={}", event.userId());
        } catch (RuntimeException e) {
            log.error("강사 지원 접수 이메일 발송 실패. userId={}", event.userId(), e);
        }
    }

    private String createContent(String name) {
        return name + "님, 강사 지원서가 정상적으로 접수되었습니다.\n"
                + "검토에는 다소 시간이 소요될 수 있으며, 승인 여부는 이메일로 다시 안내드리겠습니다.";
    }
}
