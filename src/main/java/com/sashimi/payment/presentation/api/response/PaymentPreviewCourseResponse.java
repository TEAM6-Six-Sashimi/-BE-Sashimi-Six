package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.PaymentQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentPreviewCourseResponse(

        @Schema(description = "강의 ID", example = "10")
        Long courseId,

        @Schema(description = "강의명", example = "React 완벽 가이드")
        String title,

        @Schema(description = "강의 썸네일 주소", example = "/images/react.png")
        String thumbnail,

        @Schema(description = "강사명", example = "김강사")
        String instructorName,

        @Schema(description = "강의 가격", example = "15900")
        Long price
) {
    public static PaymentPreviewCourseResponse from(
            PaymentQueryUseCase.PaymentPreviewCourse course
    ) {
        return new PaymentPreviewCourseResponse(
                course.courseId(),
                course.title(),
                course.thumbnail(),
                course.instructorName(),
                course.price()
        );
    }
}