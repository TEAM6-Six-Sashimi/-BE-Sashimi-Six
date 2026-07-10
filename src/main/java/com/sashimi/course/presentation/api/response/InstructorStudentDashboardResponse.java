package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.usecase.InstructorDashboardQueryUseCase;

import java.util.List;

public record InstructorStudentDashboardResponse(
        int totalStudentCount,
        List<CourseStudentResponse> courses
) {
    public static InstructorStudentDashboardResponse from(
            InstructorDashboardQueryUseCase.InstructorStudentDashboard dashboard
    ) {
        return new InstructorStudentDashboardResponse(
                dashboard.totalStudentCount(),
                dashboard.courses()
                        .stream()
                        .map(CourseStudentResponse::from)
                        .toList()
        );
    }

    public record CourseStudentResponse(
            Long courseId,
            String title,
            int studentCount
    ) {
        public static CourseStudentResponse from(
                InstructorDashboardQueryUseCase.CourseStudentItem item
        ) {
            return new CourseStudentResponse(
                    item.courseId(),
                    item.title(),
                    item.studentCount()
            );
        }
    }
}
