package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.usecase.InstructorDashboardQueryUseCase;

import java.util.List;

public record InstructorSalesDashboardResponse(
        int year,
        int month,
        Long totalSales,
        Long platformFee,
        Long settlementAmount,
        int platformFeeRate,
        List<CourseSalesResponse> courses
) {
    public static InstructorSalesDashboardResponse from(
            InstructorDashboardQueryUseCase.InstructorSalesDashboard dashboard
    ) {
        return new InstructorSalesDashboardResponse(
                dashboard.year(),
                dashboard.month(),
                dashboard.totalSales(),
                dashboard.platformFee(),
                dashboard.settlementAmount(),
                dashboard.platformFeeRate(),
                dashboard.courses()
                        .stream()
                        .map(CourseSalesResponse::from)
                        .toList()
        );
    }

    public record CourseSalesResponse(
            Long courseId,
            String title,
            Long salesAmount
    ) {
        public static CourseSalesResponse from(
                InstructorDashboardQueryUseCase.CourseSalesItem item
        ) {
            return new CourseSalesResponse(
                    item.courseId(),
                    item.title(),
                    item.salesAmount()
            );
        }
    }
}