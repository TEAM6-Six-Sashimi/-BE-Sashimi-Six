package com.sashimi.dashboard.application.service;

import com.sashimi.dashboard.application.port.AdminDashboardQueryPort;
import com.sashimi.dashboard.application.usecase.AdminDashboardQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminDashboardQueryService implements AdminDashboardQueryUseCase {

    private static final int PLATFORM_FEE_RATE = 30;

    private final AdminDashboardQueryPort adminDashboardQueryPort;

    @Override
    public AdminDashboardSummary getSummary() {
        long totalRevenue = zeroIfNull(
                adminDashboardQueryPort.sumCompletedCreditChargeAmount()
        );
        long courseSales = zeroIfNull(
                adminDashboardQueryPort.sumPaidCourseSalesAmount()
        );
        long subscriptionSales = zeroIfNull(
                adminDashboardQueryPort.sumPaidSubscriptionAmount()
        );

        long coursePlatformFee = courseSales * PLATFORM_FEE_RATE / 100;
        long totalSettlementAmount = courseSales - coursePlatformFee;
        long netProfit = coursePlatformFee + subscriptionSales;

        return new AdminDashboardSummary(
                totalRevenue,
                netProfit,
                totalSettlementAmount,
                PLATFORM_FEE_RATE
        );
    }

    private long zeroIfNull(Long value) {
        return value == null ? 0L : value;
    }
}