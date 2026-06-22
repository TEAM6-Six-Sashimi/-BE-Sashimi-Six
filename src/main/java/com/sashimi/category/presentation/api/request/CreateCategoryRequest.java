package com.sashimi.category.presentation.api.request;

public record CreateCategoryRequest(
        String name,
        String subCategory
) {}
