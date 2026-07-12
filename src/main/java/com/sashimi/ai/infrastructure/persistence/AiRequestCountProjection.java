package com.sashimi.ai.infrastructure.persistence;

import com.sashimi.ai.domain.model.AiFeatureType;

public interface AiRequestCountProjection {

    Integer getBucket();

    AiFeatureType getFeatureType();

    long getRequestCount();
}