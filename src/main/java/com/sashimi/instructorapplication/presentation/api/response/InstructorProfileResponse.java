package com.sashimi.instructorapplication.presentation.api.response;

import com.sashimi.instructorapplication.domain.model.InstructorApplication;
import com.sashimi.user.domain.model.User;

import java.util.List;

public record InstructorProfileResponse(
        String name,
        String profileImageUrl,
        String bio,
        List<String> mainCareers,
        String portfolioUrl
) {
    public static InstructorProfileResponse of(InstructorApplication application, User user, String profileImageUrl) {
        return new InstructorProfileResponse(
                user.getName(),
                profileImageUrl,
                application.getBio(),
                application.getMainCareers(),
                application.getPortfolioUrl()
        );
    }
}
