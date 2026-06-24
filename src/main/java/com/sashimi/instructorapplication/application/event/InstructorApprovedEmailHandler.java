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
public class InstructorApprovedEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(InstructorApprovedEvent event) {
        try {
            emailSender.send(
                    event.email(),
                    "[FitGyeok] 강사 승인이 완료되었습니다!",
                    createContent(event.name())
            );
            log.info("강사 승인 이메일 발송 완료. userId={}, email={}", event.userId(), event.email());
        } catch (RuntimeException e) {
            log.error("강사 승인 이메일 발송 실패. userId={}, email={}", event.userId(), event.email(), e);
        }
    }

    private String createContent(String name) {
        return name + "님의 강사 신청이 승인되었습니다.\n"
                + "이제 강의를 개설하고 수강생들과 지식을 나눠보세요!\n"
                + "강의 개설은 마이페이지 > 내 강의에서 시작할 수 있습니다.";
    }
}
