package com.sashimi.course.presentation.api.request;

public record CreateSessionRequest(
        String title,
        String videoUrl,
        boolean preview,
        String attachmentName,
        String attachmentUrl,
        String attachmentType,
        Long attachmentSize
) {}
