package com.sashimi.category.application.usecase;

import com.sashimi.category.application.command.CreateCategoryCommand;
import com.sashimi.category.application.command.UpdateCategoryCommand;

public interface CategoryCommandUseCase {
    Long createCategory(CreateCategoryCommand command);
    void updateCategory(UpdateCategoryCommand command);
    void deactivateCategory(Long categoryId);
}
