package com.sashimi.course.application.service;

import com.sashimi.course.application.command.*;
import com.sashimi.course.application.event.CourseApprovedEvent;
import com.sashimi.course.application.event.CourseRejectedEvent;
import com.sashimi.course.application.event.CourseSubmittedEvent;
import com.sashimi.course.application.port.CategoryPort;
import com.sashimi.course.application.port.CourseEnrollmentPort;
import com.sashimi.course.application.port.InstructorPort;
import com.sashimi.course.application.usecase.CourseCommandUseCase;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseSession;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.storage.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseCommandService implements CourseCommandUseCase {

    /** 승인일(공개 시작) 기준 공개 기간 (년) */
    private static final int PUBLICATION_PERIOD_YEARS = 2;

    /** 수강생 시청 가능 기간 (년) — 이 기간 내 등록자가 없으면 '시청자 0명' */
    private static final int STUDENT_ACCESS_PERIOD_YEARS = 2;

    private final CourseRepository courseRepository;
    private final CategoryPort categoryPort;
    private final CourseEnrollmentPort courseEnrollmentPort;
    private final FileStoragePort fileStoragePort;
    private final InstructorPort instructorPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Long createCourse(CreateCourseCommand command) {

        Long categoryId = categoryPort.getCategoryIdBySubCategoryName(command.subCategoryName());

        List<CourseSession> sessions = command.sessions().stream()
                .map(s -> CourseSession.create(s.title(), s.videoUrl(), s.durationSeconds(),
                        s.sessionOrder(), s.preview(), s.attachmentName(),
                        s.attachmentUrl(), s.attachmentType(), s.attachmentSize()))
                .toList();

        Course course = Course.create(command.instructorId(), categoryId,
                command.title(), command.description(), command.price(), command.difficulty(),
                command.thumbnail(), command.initialStatus(), sessions);

        Course saved = courseRepository.save(course);

        if (saved.getStatus() == CourseStatus.PENDING) {
            publishCourseSubmitted(saved);
        }

        return saved.getId();
    }

    @Override
    public void updateCourse(UpdateCourseCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getInstructorId().equals(command.instructorId())) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }

        if (!course.canModify()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_MODIFIABLE);
        }

        CourseStatus originalStatus = course.getStatus();

        List<CourseSession> sessions = command.sessions().stream()
                .map(s -> CourseSession.create(s.title(), s.videoUrl(), s.durationSeconds(),
                        s.sessionOrder(), s.preview(), s.attachmentName(),
                        s.attachmentUrl(), s.attachmentType(), s.attachmentSize()))
                .toList();

        Course updated = course.update(command.categoryId(),
                command.title(), command.description(), command.price(), command.difficulty(),
                command.thumbnail(), command.targetStatus(), sessions);

        Course saved = courseRepository.save(updated);

        if (originalStatus != CourseStatus.PENDING && saved.getStatus() == CourseStatus.PENDING) {
            publishCourseSubmitted(saved);
        }
    }

    @Override
    public void deleteCourse(DeleteCourseCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getInstructorId().equals(command.instructorId())) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }

        if (!course.canDelete()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_DELETABLE);
        }

        courseRepository.deleteById(command.courseId());
    }

    @Override
    public void approveCourse(ApproveCourseCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        Course approved = courseRepository.save(course.approve());

        eventPublisher.publishEvent(new CourseApprovedEvent(
                approved.getInstructorId(),
                instructorPort.getInstructorEmail(approved.getInstructorId()),
                instructorPort.getInstructorName(approved.getInstructorId()),
                approved.getId(),
                approved.getTitle()
        ));
    }

    @Override
    public void rejectCourse(RejectCourseCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        Course rejected = courseRepository.save(course.reject(command.category(), command.detail()));

        eventPublisher.publishEvent(new CourseRejectedEvent(
                rejected.getInstructorId(),
                instructorPort.getInstructorEmail(rejected.getInstructorId()),
                instructorPort.getInstructorName(rejected.getInstructorId()),
                rejected.getId(),
                rejected.getTitle(),
                command.category(),
                rejected.getRejectDetail()
        ));
    }

    private void publishCourseSubmitted(Course course) {
        eventPublisher.publishEvent(new CourseSubmittedEvent(
                course.getInstructorId(),
                instructorPort.getInstructorEmail(course.getInstructorId()),
                instructorPort.getInstructorName(course.getInstructorId()),
                course.getId(),
                course.getTitle()
        ));
    }

    @Override
    public int closeExpiredCourses() {
        LocalDateTime cutoff = LocalDateTime.now().minusYears(PUBLICATION_PERIOD_YEARS);
        List<Course> expiredCourses = courseRepository.findByStatusAndApprovedAtBefore(
                CourseStatus.APPROVED, cutoff);
        expiredCourses.forEach(course -> courseRepository.save(course.close()));
        return expiredCourses.size();
    }

    @Override
    public int archiveInactiveCourses() {
        LocalDateTime cutoff = LocalDateTime.now().minusYears(STUDENT_ACCESS_PERIOD_YEARS);
        List<Course> candidates = courseRepository.findByStatusAndArchivedFalse(CourseStatus.CLOSED);

        int archivedCount = 0;
        for (Course course : candidates) {
            // 시청 가능한 학생이 한 명이라도 있으면 건너뜀
            if (courseEnrollmentPort.hasActiveEnrollment(course.getId(), cutoff)) {
                continue;
            }
            // 영상 key에 아카이브 태그 부착 (실제 업로드된 key만; 더미 URL은 스킵)
            for (CourseSession session : course.getSessions()) {
                String key = session.getVideoUrl();
                if (key != null && !key.isBlank() && !key.startsWith("http")) {
                    fileStoragePort.archiveFile(key);
                }
            }
            courseRepository.save(course.archive());
            archivedCount++;
        }
        return archivedCount;
    }
}
