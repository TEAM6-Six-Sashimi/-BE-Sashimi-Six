package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.usecase.InstructorCourseStatisticsQueryUseCase;

import java.util.List;

public record InstructorCourseCompletionStatisticsResponse(
        List<CourseCompletionItemResponse> courses
) {
    public static InstructorCourseCompletionStatisticsResponse from(
            InstructorCourseStatisticsQueryUseCase.InstructorCourseCompletionStatistics statistics
    ) {
        return new InstructorCourseCompletionStatisticsResponse(
                statistics.courses().stream()
                        .map(CourseCompletionItemResponse::from)
                        .toList()
        );
    }

    public record CourseCompletionItemResponse(
            Long courseId,
            String title,
            int totalStudentCount,
            int completedStudentCount,
            int completionRate
    ) {
        public static CourseCompletionItemResponse from(
                InstructorCourseStatisticsQueryUseCase.CourseCompletionItem item
        ) {
            return new CourseCompletionItemResponse(
                    item.courseId(),
                    item.title(),
                    item.totalStudentCount(),
                    item.completedStudentCount(),
                    item.completionRate()
            );
        }
    }
}