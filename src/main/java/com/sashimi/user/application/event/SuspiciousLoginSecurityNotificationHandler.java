package com.sashimi.user.application.event;

import com.sashimi.verification.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuspiciousLoginSecurityNotificationHandler {

    private final EmailSender emailSender;

    // 이 이벤트는 로그인 실패(트랜잭션 롤백) 경로에서 발행되므로 AFTER_COMMIT은 절대 발화하지
    // 않는다. Redis 상태라 커밋 대기와 무관하니 일반 @EventListener로 즉시 처리한다.
    @EventListener
    public void handle(SuspiciousLoginDetectedEvent event) {
        try {
            emailSender.send(
                    event.email(),
                    "[FitGyeok] 비정상적인 로그인 시도가 감지되었습니다",
                    createContent(event.name(), event.lockDurationSeconds())
            );
            log.info("이상 로그인 알림 이메일 발송 완료. userId={}, violationCount={}",
                    event.userId(), event.violationCount());
        } catch (RuntimeException e) {
            log.error("이상 로그인 알림 이메일 발송 실패. userId={}", event.userId(), e);
        }
    }

    private String createContent(String name, long lockDurationSeconds) {
        String duration = describeDuration(lockDurationSeconds);

        return name + "님, 계정에 반복적인 로그인 실패가 감지되어 " + duration
                + " 동안 로그인이 제한됩니다.\n"
                + "본인이 시도한 것이 아니라면 비밀번호가 유출되었을 수 있으니 즉시 비밀번호를 변경해주세요.";
    }

    private String describeDuration(long seconds) {
        if (seconds >= 86400) return "1일";
        if (seconds >= 3600) return (seconds / 3600) + "시간";
        return (seconds / 60) + "분";
    }
}
