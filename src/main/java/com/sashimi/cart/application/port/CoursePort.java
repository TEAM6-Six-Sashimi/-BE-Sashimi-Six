package com.sashimi.cart.application.port;

import java.util.List;
import java.util.Map;


public interface CoursePort {

    CourseInfo getCourseInfo(Long courseId);

    Map<Long, CourseInfo> getCourseInfos(List<Long> courseIds);
}