package com.sashimi.course.application.port;

import java.util.List;

public interface CategoryPort {
    List<Long> getCategoryIdsByName(String categoryName);
}