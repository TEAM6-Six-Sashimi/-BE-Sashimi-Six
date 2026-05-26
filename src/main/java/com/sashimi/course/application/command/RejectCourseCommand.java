package com.sashimi.course.application.command;

public record RejectCourseCommand(Long courseId, String rejectReason) {}
