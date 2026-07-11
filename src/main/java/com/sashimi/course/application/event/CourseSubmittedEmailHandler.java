package com.sashimi.course.application.event;

import com.sashimi.verification.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseSubmittedEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CourseSubmittedEvent event) {
        try {
            emailSender.send(
                    event.instructorEmail(),
                    "[FitGyeok] 강의 승인 요청이 접수되었습니다",
                    createContent(event.instructorName(), event.courseTitle())
            );
            log.info("강의 승인요청 이메일 발송 완료. instructorId={}, courseId={}", event.instructorId(), event.courseId());
        } catch (RuntimeException e) {
            log.error("강의 승인요청 이메일 발송 실패. instructorId={}, courseId={}", event.instructorId(), event.courseId(), e);
        }
    }

    private String createContent(String instructorName, String courseTitle) {
        return instructorName + "님, '" + courseTitle + "' 강의의 승인 요청이 접수되었습니다.\n"
                + "검토 후 승인 여부를 이메일로 안내드리겠습니다.";
    }
}
