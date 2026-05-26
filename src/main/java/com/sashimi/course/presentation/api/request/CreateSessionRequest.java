package com.sashimi.course.presentation.api.request;

public record CreateSessionRequest(
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
