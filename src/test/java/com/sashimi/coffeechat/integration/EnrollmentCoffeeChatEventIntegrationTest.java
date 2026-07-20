package com.sashimi.coffeechat.integration;

import com.sashimi.certificate.infrastructure.CodefTokenManager;
import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;
import com.sashimi.coffeechat.domain.repository.CoffeeChatRepository;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.enrollment.application.event.EnrollmentCreatedEvent;
import com.sashimi.global.storage.FileStoragePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.verification.application.port.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "gemini"})
class EnrollmentCoffeeChatEventIntegrationTest {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(900_000_000L);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    @MockitoBean
    private EmailSender emailSender;

    @MockitoBean
    private JobPostingRecommendationAnalyzePort jobPostingRecommendationAnalyzePort;

    @MockitoBean
    private CodefTokenManager codefTokenManager;

    @MockitoBean
    private FileStoragePort fileStoragePort;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    void 수강신청_이벤트가_커밋된_후_커피챗_방이_자동생성된다() {
        Long instructorId = ID_SEQUENCE.incrementAndGet();
        Long studentId = ID_SEQUENCE.incrementAndGet();
        Long courseId = createCourse(instructorId);

        publishEnrollmentCreatedEventInRealTransaction(studentId, courseId);

        List<CoffeeChat> rooms = coffeeChatRepository.findAllByStudentId(studentId);
        assertThat(rooms).hasSize(1);
        CoffeeChat room = rooms.get(0);
        assertThat(room.getInstructorId()).isEqualTo(instructorId);
        assertThat(room.getCourseId()).isEqualTo(courseId);
        assertThat(room.getStatus()).isEqualTo(CoffeeChatStatus.PENDING);
    }

    @Test
    void 같은_학생_강사_강의_조합으로_이벤트가_두번_발행돼도_방은_한번만_생성된다() {
        Long instructorId = ID_SEQUENCE.incrementAndGet();
        Long studentId = ID_SEQUENCE.incrementAndGet();
        Long courseId = createCourse(instructorId);

        publishEnrollmentCreatedEventInRealTransaction(studentId, courseId);
        publishEnrollmentCreatedEventInRealTransaction(studentId, courseId);

        List<CoffeeChat> rooms = coffeeChatRepository.findAllByStudentId(studentId);
        assertThat(rooms).hasSize(1);
    }

    private Long createCourse(Long instructorId) {
        Course course = Course.create(
                instructorId, 1L, "통합테스트용 강의", "설명", 10_000L,
                CourseDifficulty.BEGINNER, null, CourseStatus.DRAFT, List.of());
        return courseRepository.save(course).getId();
    }

    private void publishEnrollmentCreatedEventInRealTransaction(Long studentId, Long courseId) {
        transactionTemplate.executeWithoutResult(status ->
                eventPublisher.publishEvent(new EnrollmentCreatedEvent(studentId, courseId)));
    }
}
