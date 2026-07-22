package com.sashimi.review.application.port;

import java.util.List;
import java.util.Map;

public interface ReviewCoursePort {

    String getCourseName(Long courseId);

    Map<Long, String> getCourseNamesBatch(List<Long> courseIds);
}
