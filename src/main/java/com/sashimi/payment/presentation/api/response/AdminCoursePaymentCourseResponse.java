package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.AdminPaymentQueryUseCase;

public record AdminCoursePaymentCourseResponse(
        String courseTitle,
        Long price
) {

    public static AdminCoursePaymentCourseResponse from(
            AdminPaymentQueryUseCase.AdminCoursePaymentCourse course
    ) {
        return new AdminCoursePaymentCourseResponse(
                course.courseTitle(),
                course.price()
        );
    }
}