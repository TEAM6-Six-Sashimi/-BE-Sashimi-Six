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
public class UserPasswordChangedSecurityNotificationHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserPasswordChangedEvent event) {
        emailSender.send(
                event.email(),
                "[Sashimi Six] 비밀번호가 변경되었습니다.",
                createContent()
        );

        log.info("Password change security notification sent. userId={}", event.userId());
    }

    private String createContent() {
        return "회원님의 비밀번호가 변경되었습니다.\n"
                + "본인이 변경한 것이 아니라면 즉시 비밀번호를 재설정해주세요.";
    }
}