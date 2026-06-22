package com.sashimi.review.application.command;

public record WriteReviewCommand(
        Long userId,
        Long courseId,
        int rating,
        String content
) {}
