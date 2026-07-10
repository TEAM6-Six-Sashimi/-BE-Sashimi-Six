package com.sashimi.course.application.usecase;

import java.util.List;

public interface InstructorCourseStatisticsQueryUseCase {

    InstructorCourseSalesStatistics getMonthlySales(
            Long instructorId,
            Integer year,
            Integer month
    );

    InstructorCourseStudentStatistics getStudentCounts(Long instructorId);

    InstructorCourseCompletionStatistics getCompletionRates(Long instructorId);

    record InstructorCourseSalesStatistics(
            int year,
            int month,
            Long totalSales,
            List<CourseSalesItem> courses
    ) {
    }

    record CourseSalesItem(
            Long courseId,
            String title,
            Long salesAmount
    ) {
    }

    record InstructorCourseStudentStatistics(
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

    record InstructorCourseCompletionStatistics(
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
}