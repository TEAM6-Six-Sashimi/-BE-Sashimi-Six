package com.sashimi.dashboard.application.usecase;

public interface AdminDashboardQueryUseCase {

    AdminDashboardSummary getSummary();

    record AdminDashboardSummary(
            Long totalRevenue,
            Long netProfit,
            Long totalSettlementAmount,
            int platformFeeRate
    ) {
    }
}