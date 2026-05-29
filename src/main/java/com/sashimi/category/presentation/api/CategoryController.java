package com.sashimi.category.presentation.api;

import com.sashimi.category.application.usecase.CategoryQueryUseCase;
import com.sashimi.category.domain.model.Category;
import com.sashimi.category.presentation.api.response.CategoryGroupResponse;
import com.sashimi.category.presentation.api.response.CategoryGroupResponse.CategoryOptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryQueryUseCase categoryQueryUseCase;

    public CategoryController(CategoryQueryUseCase categoryQueryUseCase) {
        this.categoryQueryUseCase = categoryQueryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CategoryGroupResponse>> getCategories() {
        List<Category> categories = categoryQueryUseCase.getActiveCategories();

        Map<String, List<Category>> grouped = new LinkedHashMap<>();
        for (Category category : categories) {
            grouped.computeIfAbsent(category.getName(), k -> new ArrayList<>())
                    .add(category);
        }

        List<CategoryGroupResponse> response = grouped.entrySet().stream()
                .map(e -> new CategoryGroupResponse(
                        e.getKey(),
                        e.getValue().stream()
                                .map(category -> new CategoryOptionResponse(
                                        category.getId(),
                                        category.getSubCategory()
                                ))
                                .toList()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
