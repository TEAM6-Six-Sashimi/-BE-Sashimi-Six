package com.sashimi.category.presentation.api.response;

import com.sashimi.category.domain.model.Category;

public record AdminCategoryResponse(
        Long id,
        String code,
        String mainCategoryName,
        String subCategory,
        boolean active
) {
    public static AdminCategoryResponse from(Category category) {
        return new AdminCategoryResponse(
                category.getId(),
                String.format("cat-%03d", category.getId()),
                category.getName(),
                category.getSubCategory(),
                category.isActive()
        );
    }
}
