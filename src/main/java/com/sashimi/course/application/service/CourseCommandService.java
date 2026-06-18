package com.sashimi.course.application.service;

import com.sashimi.course.application.command.*;
import com.sashimi.course.application.port.CategoryPort;
import com.sashimi.course.application.usecase.CourseCommandUseCase;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseSession;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseCommandService implements CourseCommandUseCase {

    private final CourseRepository courseRepository;
    private final CategoryPort categoryPort;

    public CourseCommandService(CourseRepository courseRepository, CategoryPort categoryPort) {
        this.courseRepository = courseRepository;
        this.categoryPort = categoryPort;
    }

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

        if (!course.canModify()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_MODIFIABLE);
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
}
