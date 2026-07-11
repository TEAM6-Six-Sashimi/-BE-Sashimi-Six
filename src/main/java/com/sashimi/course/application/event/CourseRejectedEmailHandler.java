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
public class CourseRejectedEmailHandler {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CourseRejectedEvent event) {
        try {
            emailSender.send(
                    event.instructorEmail(),
                    "[FitGyeok] 강의 승인 요청 결과 안내",
                    createContent(event.instructorName(), event.courseTitle(), event.category(), event.detail())
            );
            log.info("강의 반려 이메일 발송 완료. instructorId={}, courseId={}", event.instructorId(), event.courseId());
        } catch (RuntimeException e) {
            log.error("강의 반려 이메일 발송 실패. instructorId={}, courseId={}", event.instructorId(), event.courseId(), e);
        }
    }

    private String createContent(String instructorName, String courseTitle,
                                  com.sashimi.course.domain.model.RejectReasonCategory category, String detail) {
        String reason = (detail == null || detail.isBlank())
                ? category.getLabel()
                : category.getLabel() + " (" + detail + ")";

        return instructorName + "님, 아쉽지만 '" + courseTitle + "' 강의는 이번에 승인되지 않았습니다.\n"
                + "사유: " + reason + "\n"
                + "내용을 보완하여 다시 제출해 주세요.";
    }
}
