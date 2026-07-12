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
public class CourseApprovedEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CourseApprovedEvent event) {
        try {
            emailSender.send(
                    event.instructorEmail(),
                    "[FitGyeok] 강의가 승인되었습니다",
                    createContent(event.instructorName(), event.courseTitle())
            );
            log.info("강의 승인 이메일 발송 완료. instructorId={}, courseId={}", event.instructorId(), event.courseId());
        } catch (RuntimeException e) {
            log.error("강의 승인 이메일 발송 실패. instructorId={}, courseId={}", event.instructorId(), event.courseId(), e);
        }
    }

    private String createContent(String instructorName, String courseTitle) {
        return instructorName + "님, '" + courseTitle + "' 강의가 승인되어 공개되었습니다.\n"
                + "지금 바로 수강생들에게 강의를 선보여보세요!";
    }
}
