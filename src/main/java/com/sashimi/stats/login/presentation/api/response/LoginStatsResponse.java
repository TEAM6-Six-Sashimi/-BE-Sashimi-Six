package com.sashimi.stats.login.presentation.api.response;

import com.sashimi.stats.login.application.usecase.AdminLoginStatsQueryUseCase;

import java.util.List;

public record LoginStatsResponse(
        String period,
        List<LoginStatsPointResponse> data
) {

    public static LoginStatsResponse from(AdminLoginStatsQueryUseCase.LoginStats stats) {
        return new LoginStatsResponse(
                stats.period().name().toLowerCase(),
                stats.data().stream()
                        .map(LoginStatsPointResponse::from)
                        .toList()
        );
    }

    public record LoginStatsPointResponse(
            String label,
            long count
    ) {

        public static LoginStatsPointResponse from(AdminLoginStatsQueryUseCase.LoginStatsItem item) {
            return new LoginStatsPointResponse(item.label(), item.count());
        }
    }
}
