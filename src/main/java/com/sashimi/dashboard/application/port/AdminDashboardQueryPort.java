package com.sashimi.dashboard.application.port;

public interface AdminDashboardQueryPort {

    Long sumCompletedCreditChargeAmount();

    Long sumPaidCourseSalesAmount();

    Long sumPaidSubscriptionAmount();

    long countActiveStudents();

    long countActiveInstructors();

    long countApprovedCourses();
}