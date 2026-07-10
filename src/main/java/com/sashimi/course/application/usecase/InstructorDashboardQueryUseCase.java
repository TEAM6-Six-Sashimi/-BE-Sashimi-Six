package com.sashimi.course.application.usecase;

import java.util.List;

public interface InstructorDashboardQueryUseCase {

    InstructorSalesDashboard getMonthlySales(
            Long instructorId,
            Integer year,
            Integer month
    );

    InstructorStudentDashboard getStudentCounts(Long instructorId);

    record InstructorStudentDashboard(
            int totalStudentCount,
            List<CourseStudentItem> courses
    ) {
    }

    record CourseStudentItem(
            Long courseId,
            String title,
            int studentCount
    ) {
    }

    InstructorCompletionDashboard getCompletionRates(Long instructorId);

    record InstructorCompletionDashboard(
            List<CourseCompletionItem> courses
    ) {
    }

    record CourseCompletionItem(
            Long courseId,
            String title,
            int totalStudentCount,
            int completedStudentCount,
            int completionRate
    ) {
    }

    record InstructorSalesDashboard(
            int year,
            int month,
            Long totalSales,
            Long platformFee,
            Long settlementAmount,
            int platformFeeRate,
            List<CourseSalesItem> courses
    ) {
    }

    record CourseSalesItem(
            Long courseId,
            String title,
            Long salesAmount
    ) {
    }

}
