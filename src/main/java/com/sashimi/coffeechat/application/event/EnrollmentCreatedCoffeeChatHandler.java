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
        createIfNotExists(event.userId(), event.courseId());
    }

    public boolean createIfNotExists(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (coffeeChatRepository.existsByStudentIdAndInstructorIdAndCourseId(
                studentId, course.getInstructorId(), courseId)) {
            return false;
        }

        coffeeChatRepository.save(
                CoffeeChat.create(studentId, course.getInstructorId(), courseId));

        log.info("커피챗 방 자동생성 - studentId={}, instructorId={}, courseId={}",
                studentId, course.getInstructorId(), courseId);
        return true;
    }
}
