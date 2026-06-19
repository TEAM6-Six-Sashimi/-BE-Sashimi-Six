package com.sashimi.course.application.service;

import com.sashimi.course.application.usecase.CourseQueryUseCase;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseQueryService implements CourseQueryUseCase {

    private final CourseRepository courseRepository;

    @Override
    public List<Course> getApprovedCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorIdAndStatus(instructorId, CourseStatus.APPROVED);
    }

    @Override
    public List<Course> getInProgressCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorIdAndStatusIn(instructorId,
                List.of(CourseStatus.DRAFT, CourseStatus.PENDING, CourseStatus.REJECTED));
    }

    @Override
    public List<Course> getClosedCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorIdAndStatus(instructorId, CourseStatus.CLOSED);
    }

    @Override
    public Course getCourseDetail(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }

        return course;
    }

    @Override
    public List<Course> getAllCoursesForAdmin() {
        return courseRepository.findByStatusIn(List.of(CourseStatus.APPROVED, CourseStatus.CLOSED));
    }

    @Override
    public List<Course> getPendingCoursesForAdmin() {
        return courseRepository.findByStatus(CourseStatus.PENDING);
    }

    @Override
    public List<Course> getRejectedCoursesForAdmin() {
        return courseRepository.findByStatus(CourseStatus.REJECTED);
    }

    @Override
    public List<Course> getClosedCoursesForAdmin() {
        return courseRepository.findByStatus(CourseStatus.CLOSED);
    }
}
