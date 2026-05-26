package com.sashimi.member.presentation.api.request;

public record ApplyInstructorRequest(
        String bio,
        String career,
        String portfolioUrl
) {
}