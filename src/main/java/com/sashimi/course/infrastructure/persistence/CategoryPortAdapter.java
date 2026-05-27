package com.sashimi.course.infrastructure.persistence;

import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.course.application.port.CategoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryPortAdapter implements CategoryPort {

    private final CategoryRepository categoryRepository;

    public CategoryPortAdapter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Long> getCategoryIdsByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .stream()
                .map(category -> category.getId())
                .toList();
    }
}