package com.sashimi.enrollment.application.service;

import com.sashimi.enrollment.application.port.CoursePort;
import com.sashimi.enrollment.application.port.EnrolledCourseInfo;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.enrollment.application.query.EnrolledCourseView;
import com.sashimi.enrollment.application.usecase.StudentCourseQueryUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class StudentCourseQueryService implements StudentCourseQueryUseCase {

    private final EnrollmentPort enrollmentPort;
    private final CoursePort coursePort;

    public StudentCourseQueryService(EnrollmentPort enrollmentPort, CoursePort coursePort) {
        this.enrollmentPort = enrollmentPort;
        this.coursePort = coursePort;
    }

    @Override
    public List<EnrolledCourseView> getEnrolledCourses(Long userId) {
        return enrollmentPort.getEnrollmentsByUser(userId).stream()
                .map(summary -> {
                    EnrolledCourseInfo courseInfo = coursePort.getCourseInfo(summary.courseId());
                    return new EnrolledCourseView(
                            summary.courseId(),
                            courseInfo.title(),
                            courseInfo.thumbnail(),
                            courseInfo.instructorName(),
                            summary.progressRate(),
                            summary.completed()
                    );
                })
                .toList();
    }
}