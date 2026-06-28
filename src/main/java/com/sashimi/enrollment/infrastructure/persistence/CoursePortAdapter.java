package com.sashimi.enrollment.infrastructure.persistence;

import com.sashimi.category.domain.model.Category;
import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.enrollment.application.port.CourseDetailInfo;
import com.sashimi.enrollment.application.port.CoursePort;
import com.sashimi.enrollment.application.port.EnrolledCourseInfo;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("enrollmentCoursePortAdapter")
public class CoursePortAdapter implements CoursePort {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CoursePortAdapter(CourseRepository courseRepository,
                             UserRepository userRepository,
                             CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public EnrolledCourseInfo getCourseInfo(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        User instructor = userRepository.findById(course.getInstructorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Category category = categoryRepository.findById(course.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return new EnrolledCourseInfo(
                course.getTitle(),
                course.getThumbnail(),
                instructor.getName(),
                category.getId(),
                category.getSubCategory(),
                category.getName()
        );
    }

    @Override
    public CourseDetailInfo getCourseDetailInfo(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        User instructor = userRepository.findById(course.getInstructorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        String categoryName = categoryRepository.findById(course.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND))
                .getSubCategory();

        List<CourseDetailInfo.SessionInfo> sessions = course.getSessions().stream()
                .map(s -> new CourseDetailInfo.SessionInfo(
                        s.getId(), s.getSessionUid(), s.getTitle(), s.getVideoUrl(),
                        s.getDurationSeconds(), s.getSessionOrder(), s.isPreview(),
                        s.getAttachmentName(), s.getAttachmentUrl(),
                        s.getAttachmentType(), s.getAttachmentSize()
                ))
                .toList();

        return new CourseDetailInfo(
                course.getId(), course.getTitle(), course.getDescription(),
                course.getPrice(), course.getDifficulty(), course.getThumbnail(),
                course.getTotalDuration(), course.getRatingAvg(), course.getReviewCount(),
                course.getStudentCount(), instructor.getName(), categoryName, sessions
        );
    }
}
