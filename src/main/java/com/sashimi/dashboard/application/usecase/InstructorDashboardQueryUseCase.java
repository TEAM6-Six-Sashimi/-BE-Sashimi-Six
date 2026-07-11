package com.sashimi.dashboard.application.usecase;

public interface InstructorDashboardQueryUseCase {

    InstructorDashboardSummary getMonthlySummary(
            Long instructorId,
            Integer year,
            Integer month
    );

    record InstructorDashboardSummary(
            int year,
            int month,
            Long totalSales,
            Long platformFee,
            Long settlementAmount,
            int platformFeeRate
    ) {
    }
}