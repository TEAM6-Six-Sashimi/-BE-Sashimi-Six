package com.sashimi.category.application.command;

public record CreateCategoryCommand(
        String name,
        String subCategory
) {}
