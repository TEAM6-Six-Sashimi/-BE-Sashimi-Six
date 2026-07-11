package com.sashimi.course.application.event;

public record CourseSubmittedEvent(
        Long instructorId,
        String instructorEmail,
        String instructorName,
        Long courseId,
        String courseTitle
) {
}
