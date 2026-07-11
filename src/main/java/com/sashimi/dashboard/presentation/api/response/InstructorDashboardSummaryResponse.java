package com.sashimi.dashboard.presentation.api.response;

import com.sashimi.dashboard.application.usecase.InstructorDashboardQueryUseCase;

public record InstructorDashboardSummaryResponse(
        int year,
        int month,
        Long totalSales,
        Long platformFee,
        Long settlementAmount,
        int platformFeeRate
) {

    public static InstructorDashboardSummaryResponse from(
            InstructorDashboardQueryUseCase.InstructorDashboardSummary summary
    ) {
        return new InstructorDashboardSummaryResponse(
                summary.year(),
                summary.month(),
                summary.totalSales(),
                summary.platformFee(),
                summary.settlementAmount(),
                summary.platformFeeRate()
        );
    }
}