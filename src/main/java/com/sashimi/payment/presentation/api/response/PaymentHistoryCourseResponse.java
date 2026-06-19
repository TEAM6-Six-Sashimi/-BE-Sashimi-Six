package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.PaymentQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentHistoryCourseResponse(

        @Schema(description = "강의 ID", example = "10")
        Long courseId,

        @Schema(description = "결제 당시 강의명", example = "React 완벽 가이드")
        String title,

        @Schema(description = "결제 당시 가격", example = "15900")
        Long price
) {
    public static PaymentHistoryCourseResponse from(
            PaymentQueryUseCase.PaymentHistoryCourse course
    ) {
        return new PaymentHistoryCourseResponse(
                course.courseId(),
                course.title(),
                course.price()
        );
    }
}