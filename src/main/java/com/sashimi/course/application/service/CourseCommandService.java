package com.sashimi.course.application.service;

import com.sashimi.course.application.command.*;
import com.sashimi.course.application.port.CategoryPort;
import com.sashimi.course.application.usecase.CourseCommandUseCase;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseSession;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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

    private final CourseRepository courseRepository;
    private final CategoryPort categoryPort;

    @Override
    public Long createCourse(CreateCourseCommand command) {
        Long categoryId = categoryPort.getCategoryIdBySubCategoryName(command.subCategoryName());

        List<CourseSession> sessions = command.sessions().stream()
                .map(s -> CourseSession.create(s.title(), s.videoUrl(), s.durationSeconds(),
                        s.sessionOrder(), s.preview(), s.attachmentName(),
                        s.attachmentUrl(), s.attachmentType(), s.attachmentSize()))
                .toList();

        Course course = Course.create(command.instructorId(), categoryId, command.ncsInfoId(),
                command.title(), command.description(), command.price(), command.difficulty(),
                command.thumbnail(), command.initialStatus(), sessions);

        return courseRepository.save(course).getId();
    }

    @Override
    public void updateCourse(UpdateCourseCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getInstructorId().equals(command.instructorId())) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }

        List<CourseSession> sessions = command.sessions().stream()
                .map(s -> CourseSession.create(s.title(), s.videoUrl(), s.durationSeconds(),
                        s.sessionOrder(), s.preview(), s.attachmentName(),
                        s.attachmentUrl(), s.attachmentType(), s.attachmentSize()))
                .toList();

        Course updated = course.update(command.categoryId(), command.ncsInfoId(),
                command.title(), command.description(), command.price(), command.difficulty(),
                command.thumbnail(), command.targetStatus(), sessions);

        courseRepository.save(updated);
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

        courseRepository.save(course.approve());
    }

    @Override
    public void rejectCourse(RejectCourseCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        courseRepository.save(course.reject(command.rejectReason()));
    }

    @Override
    public int closeExpiredCourses() {
        LocalDateTime cutoff = LocalDateTime.now().minusYears(PUBLICATION_PERIOD_YEARS);
        List<Course> expiredCourses = courseRepository.findByStatusAndApprovedAtBefore(
                CourseStatus.APPROVED, cutoff);
        expiredCourses.forEach(course -> courseRepository.save(course.close()));
        return expiredCourses.size();
    }
}
