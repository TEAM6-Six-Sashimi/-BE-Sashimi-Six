package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.usecase.InstructorCourseStatisticsQueryUseCase;

import java.util.List;

public record InstructorCourseStudentStatisticsResponse(
        int totalStudentCount,
        List<CourseStudentItemResponse> courses
) {
    public static InstructorCourseStudentStatisticsResponse from(
            InstructorCourseStatisticsQueryUseCase.InstructorCourseStudentStatistics statistics
    ) {
        return new InstructorCourseStudentStatisticsResponse(
                statistics.totalStudentCount(),
                statistics.courses().stream()
                        .map(CourseStudentItemResponse::from)
                        .toList()
        );
    }

    public record CourseStudentItemResponse(
            Long courseId,
            String title,
            int studentCount
    ) {
        public static CourseStudentItemResponse from(
                InstructorCourseStatisticsQueryUseCase.CourseStudentItem item
        ) {
            return new CourseStudentItemResponse(
                    item.courseId(),
                    item.title(),
                    item.studentCount()
            );
        }
    }
}