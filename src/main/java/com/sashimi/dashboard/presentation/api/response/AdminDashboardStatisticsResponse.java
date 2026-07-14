package com.sashimi.dashboard.presentation.api.response;

import com.sashimi.dashboard.application.usecase.AdminDashboardQueryUseCase;

public record AdminDashboardStatisticsResponse(
        long totalMembers,
        long studentCount,
        long instructorCount,
        long totalCourses
) {

    public static AdminDashboardStatisticsResponse from(
            AdminDashboardQueryUseCase.AdminDashboardStatistics statistics
    ) {
        return new AdminDashboardStatisticsResponse(
                statistics.totalMembers(),
                statistics.studentCount(),
                statistics.instructorCount(),
                statistics.totalCourses()
        );
    }
}
