package com.sashimi.course.application.event;

public record CourseApprovedEvent(
        Long instructorId,
        String instructorEmail,
        String instructorName,
        Long courseId,
        String courseTitle
) {
}
