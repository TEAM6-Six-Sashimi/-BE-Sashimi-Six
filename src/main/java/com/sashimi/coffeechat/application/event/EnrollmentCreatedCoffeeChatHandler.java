package com.sashimi.coffeechat.application.event;

import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.repository.CoffeeChatRepository;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.enrollment.application.event.EnrollmentCreatedEvent;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentCreatedCoffeeChatHandler {

    private final CoffeeChatRepository coffeeChatRepository;
    private final CourseRepository courseRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(EnrollmentCreatedEvent event) {
        Course course = courseRepository.findById(event.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (coffeeChatRepository.existsByStudentIdAndInstructorIdAndCourseId(
                event.userId(), course.getInstructorId(), event.courseId())) {
            return;
        }

        coffeeChatRepository.save(
                CoffeeChat.create(event.userId(), course.getInstructorId(), event.courseId()));

        log.info("커피챗 방 자동생성 - studentId={}, instructorId={}, courseId={}",
                event.userId(), course.getInstructorId(), event.courseId());
    }
}
