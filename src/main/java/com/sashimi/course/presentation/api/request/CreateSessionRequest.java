package com.sashimi.course.presentation.api.request;

public record CreateSessionRequest(
        String title,
        String videoUrl
) {}
