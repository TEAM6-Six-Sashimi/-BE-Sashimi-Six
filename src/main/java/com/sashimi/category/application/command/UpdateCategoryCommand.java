package com.sashimi.category.application.command;

public record UpdateCategoryCommand(
        Long categoryId,
        String subCategory
) {}
