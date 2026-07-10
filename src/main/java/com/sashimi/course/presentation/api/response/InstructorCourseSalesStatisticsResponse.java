package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.usecase.InstructorCourseStatisticsQueryUseCase;

import java.util.List;

public record InstructorCourseSalesStatisticsResponse(
        int year,
        int month,
        Long totalSales,
        List<CourseSalesItemResponse> courses
) {
    public static InstructorCourseSalesStatisticsResponse from(
            InstructorCourseStatisticsQueryUseCase.InstructorCourseSalesStatistics statistics
    ) {
        return new InstructorCourseSalesStatisticsResponse(
                statistics.year(),
                statistics.month(),
                statistics.totalSales(),
                statistics.courses().stream()
                        .map(CourseSalesItemResponse::from)
                        .toList()
        );
    }

    public record CourseSalesItemResponse(
            Long courseId,
            String title,
            Long salesAmount
    ) {
        public static CourseSalesItemResponse from(
                InstructorCourseStatisticsQueryUseCase.CourseSalesItem item
        ) {
            return new CourseSalesItemResponse(
                    item.courseId(),
                    item.title(),
                    item.salesAmount()
            );
        }
    }
}