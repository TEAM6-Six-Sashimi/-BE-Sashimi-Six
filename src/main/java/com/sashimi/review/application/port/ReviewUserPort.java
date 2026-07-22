package com.sashimi.review.application.port;

import java.util.List;
import java.util.Map;

public interface ReviewUserPort {

    String getUserLoginId(Long userId);

    Map<Long, String> getUserLoginIdsBatch(List<Long> userIds);
}
