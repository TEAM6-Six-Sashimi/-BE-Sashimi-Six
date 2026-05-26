package com.sashimi.cart.infrastructure.persistence;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.application.port.CoursePort;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class CoursePortAdapter implements CoursePort {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CoursePortAdapter(
            CourseRepository courseRepository,
            UserRepository userRepository
    ) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CourseInfo getCourseInfo(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        User instructor = userRepository.findById(course.getInstructorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new CourseInfo(
                course.getId(),
                course.getTitle(),
                course.getPrice(),
                course.getThumbnail(),
                instructor.getName(),
                course.getStatus() == CourseStatus.APPROVED
        );
    }
}