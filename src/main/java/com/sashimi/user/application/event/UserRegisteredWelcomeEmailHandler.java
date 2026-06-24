package com.sashimi.user.application.event;

import com.sashimi.verification.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredWelcomeEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserRegisteredEvent event) {
        emailSender.send(
                event.email(),
                "[FitGyeok] 가입을 환영합니다!",
                createContent(event.name())
        );

        log.info("환영 이메일 발송 완료. userId={}, email={}", event.userId(), event.email());
    }

    private String createContent(String name) {
        return name + "님, FitGyeok에 오신 걸 환영합니다!\n"
                + "다양한 강의를 통해 새로운 것을 배워보세요.";
    }
}
