package com.sashimi.course.application.port;

import java.util.List;
import java.util.Map;

public interface InstructorPort {
    String getInstructorName(Long instructorId);
    String getInstructorEmail(Long instructorId);
    String getInstructorLoginId(Long instructorId);
    InstructorInfo getInstructorInfo(Long instructorId);
    Map<Long, InstructorInfo> getInstructorInfoBatch(List<Long> instructorIds);

    record InstructorInfo(
            String name,
            String profileImagePath,
            String bio,
            List<String> mainCareers,
            String portfolioUrl
    ) {}
}