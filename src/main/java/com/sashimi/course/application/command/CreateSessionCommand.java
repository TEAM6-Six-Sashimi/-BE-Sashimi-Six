package com.sashimi.course.application.command;

public record CreateSessionCommand(
        String title,
        String videoUrl,
        int durationSeconds,
        int sessionOrder,
        boolean preview,
        String attachmentName,
        String attachmentUrl,
        String attachmentType,
        Long attachmentSize
) {}
