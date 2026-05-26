package com.sashimi.course.application.command;

public record DeleteCourseCommand(
        Long courseId,
        Long instructorId
) {}
