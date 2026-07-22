package com.sashimi.enrollment.application.event;

public record EnrollmentCreatedEvent(Long userId, Long courseId) {}
