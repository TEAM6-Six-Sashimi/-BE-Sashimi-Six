package com.sashimi.enrollment.application.port;

public record EnrolledCourseInfo(
        String title,
        String thumbnail,
        String instructorName
) {}