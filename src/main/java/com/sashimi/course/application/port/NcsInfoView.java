package com.sashimi.course.application.port;

import java.util.List;

public record NcsInfoView(
        String categoryPath,
        String jobDescription,
        List<String> abilityUnitNames,
        int totalAbilityUnitCount
) {}