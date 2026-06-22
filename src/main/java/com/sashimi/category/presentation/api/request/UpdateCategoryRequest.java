package com.sashimi.category.presentation.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(
        @NotBlank(message = "세부 카테고리명은 필수입니다.") String subCategory
) {}
