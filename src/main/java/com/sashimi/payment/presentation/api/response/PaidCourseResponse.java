package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.PaymentCommandUseCase;


public record PaidCourseResponse(Long courseId, String title, Long price) {

    public static PaidCourseResponse from(PaymentCommandUseCase.PaidCourse course) {
        return new PaidCourseResponse(course.courseId(), course.title(), course.price());
    }
}