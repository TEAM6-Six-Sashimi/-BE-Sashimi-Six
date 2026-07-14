package com.sashimi.stats.login.application.usecase;

import java.util.List;

public interface AdminLoginStatsQueryUseCase {

    LoginStats getStats(LoginStatsPeriod period);

    enum LoginStatsPeriod {
        HOURLY,
        DAILY
    }

    record LoginStats(
            LoginStatsPeriod period,
            List<LoginStatsItem> data
    ) {
    }

    record LoginStatsItem(
            String label,
            long count
    ) {
    }
}
