package com.sashimi.enrollment.application.port;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface EnrollmentPort {

    boolean isEnrolled(Long userId, Long courseId);

    Set<Long> findEnrolledCourseIds(
            Long userId,
            List<Long> courseIds
    );

    void enrollPaidCourse(Long userId, Long courseId, Long orderItemId);

    List<EnrollmentSummary> getEnrollmentsByUser(Long userId);

    Optional<EnrollmentSummary> getEnrollmentByCourse(Long userId, Long courseId);

    void forEachPaidEnrollment(Consumer<PaidEnrollment> consumer);
}