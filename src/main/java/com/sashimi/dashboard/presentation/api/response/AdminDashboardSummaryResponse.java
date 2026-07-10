package com.sashimi.dashboard.presentation.api.response;

import com.sashimi.dashboard.application.usecase.AdminDashboardQueryUseCase;

public record AdminDashboardSummaryResponse(
        Long totalRevenue,
        Long netProfit,
        Long totalSettlementAmount,
        int platformFeeRate
) {

    public static AdminDashboardSummaryResponse from(
            AdminDashboardQueryUseCase.AdminDashboardSummary summary
    ) {
        return new AdminDashboardSummaryResponse(
                summary.totalRevenue(),
                summary.netProfit(),
                summary.totalSettlementAmount(),
                summary.platformFeeRate()
        );
    }
}