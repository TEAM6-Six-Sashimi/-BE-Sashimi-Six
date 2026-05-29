package com.sashimi.category.presentation.api.response;

import java.util.List;

public record CategoryGroupResponse(
        String name,
        List<CategoryOptionResponse> options
) {
    public record CategoryOptionResponse(Long id, String name) {}
}