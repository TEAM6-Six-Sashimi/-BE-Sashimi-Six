package com.sashimi.course.application.port;

import java.util.List;

public interface InstructorPort {
    String getInstructorName(Long instructorId);
    String getInstructorEmail(Long instructorId);
    String getInstructorLoginId(Long instructorId);
    InstructorInfo getInstructorInfo(Long instructorId);

    record InstructorInfo(
            String name,
            String profileImagePath,
            String bio,
            List<String> mainCareers,
            String portfolioUrl
    ) {}
}