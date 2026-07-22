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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.COURSE_NOT_FOUND)
                );

        User instructor = userRepository.findById(course.getInstructorId())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

        return toCourseInfo(course, instructor);
    }

    @Override
    public Map<Long, CourseInfo> getCourseInfos(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Map.of();
        }

        List<Long> distinctCourseIds = courseIds.stream()
                .distinct()
                .toList();

        List<Course> courses =
                courseRepository.findAllByIdIn(distinctCourseIds);

        if (courses.size() != distinctCourseIds.size()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        List<Long> instructorIds = courses.stream()
                .map(Course::getInstructorId)
                .distinct()
                .toList();

        Map<Long, User> instructorMap =
                userRepository.findAllByIdIn(instructorIds)
                        .stream()
                        .collect(Collectors.toMap(
                                User::getId,
                                Function.identity()
                        ));

        if (instructorMap.size() != instructorIds.size()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return courses.stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        course -> {
                            User instructor =
                                    instructorMap.get(
                                            course.getInstructorId()
                                    );

                            return toCourseInfo(
                                    course,
                                    instructor
                            );
                        }
                ));
    }

    private CourseInfo toCourseInfo(
            Course course,
            User instructor
    ) {
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