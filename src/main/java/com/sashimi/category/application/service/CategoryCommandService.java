package com.sashimi.category.application.service;

import com.sashimi.category.application.command.CreateCategoryCommand;
import com.sashimi.category.application.command.UpdateCategoryCommand;
import com.sashimi.category.application.usecase.CategoryCommandUseCase;
import com.sashimi.category.domain.model.Category;
import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryCommandService implements CategoryCommandUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    public Long createCategory(CreateCategoryCommand command) {
        if (categoryRepository.existsBySubCategory(command.subCategory())) {
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATE);
        }
        Long mainCategoryId = categoryRepository.findMainCategoryIdByName(command.name())
                .orElseGet(() -> categoryRepository.findMaxMainCategoryId() + 1);
        Category category = Category.create(mainCategoryId, command.name(), command.subCategory(), 0);
        return categoryRepository.save(category).getId();
    }

    @Override
    public void updateCategory(UpdateCategoryCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        if (!category.getSubCategory().equals(command.subCategory())
                && categoryRepository.existsBySubCategory(command.subCategory())) {
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATE);
        }
        categoryRepository.save(category.update(command.subCategory()));
    }

    @Override
    public void deactivateCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.save(category.deactivate());
    }
}
