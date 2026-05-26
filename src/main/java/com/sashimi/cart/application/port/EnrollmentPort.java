package com.sashimi.cart.application.port;

public interface EnrollmentPort {

    boolean isEnrolled(Long userId, Long courseId);

    void enrollPaidCourse(Long userId, Long courseId, Long orderItemId);
}