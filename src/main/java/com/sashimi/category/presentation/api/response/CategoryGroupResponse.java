package com.sashimi.category.presentation.api.response;

import java.util.List;

public record CategoryGroupResponse(
        Long mainCategoryId,
        String name,
        List<String> subCategories,
        List<CategoryOptionResponse> options
) {
    public record CategoryOptionResponse(Long id, String name) {}
}