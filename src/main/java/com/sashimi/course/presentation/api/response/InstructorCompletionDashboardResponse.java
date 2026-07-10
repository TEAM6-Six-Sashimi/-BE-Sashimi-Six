package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.usecase.InstructorDashboardQueryUseCase;

import java.util.List;

public record InstructorCompletionDashboardResponse(
        List<CourseCompletionResponse> courses
) {
    public static InstructorCompletionDashboardResponse from(
            InstructorDashboardQueryUseCase.InstructorCompletionDashboard dashboard
    ) {
        return new InstructorCompletionDashboardResponse(
                dashboard.courses()
                        .stream()
                        .map(CourseCompletionResponse::from)
                        .toList()
        );
    }

    public record CourseCompletionResponse(
            Long courseId,
            String title,
            int totalStudentCount,
            int completedStudentCount,
            int completionRate
    ) {
        public static CourseCompletionResponse from(
                InstructorDashboardQueryUseCase.CourseCompletionItem item
        ) {
            return new CourseCompletionResponse(
                    item.courseId(),
                    item.title(),
                    item.totalStudentCount(),
                    item.completedStudentCount(),
                    item.completionRate()
            );
        }
    }
}
