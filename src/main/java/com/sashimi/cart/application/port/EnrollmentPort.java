package com.sashimi.cart.application.port;

public interface EnrollmentPort {

    boolean isEnrolled(Long userId, Long courseId);
}