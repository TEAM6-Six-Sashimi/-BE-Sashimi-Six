package com.sashimi.dashboard.application.usecase;

public interface AdminDashboardQueryUseCase {

    AdminDashboardSummary getSummary();

    AdminDashboardStatistics getStatistics();

    record AdminDashboardSummary(
            Long totalRevenue,
            Long netProfit,
            Long totalSettlementAmount,
            int platformFeeRate
    ) {
    }

    record AdminDashboardStatistics(
            long totalMembers,
            long studentCount,
            long instructorCount,
            long totalCourses
    ) {
    }
}