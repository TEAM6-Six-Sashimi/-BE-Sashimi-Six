package com.sashimi.category.presentation.api;

import com.sashimi.category.application.command.CreateCategoryCommand;
import com.sashimi.category.application.command.UpdateCategoryCommand;
import com.sashimi.category.application.usecase.CategoryCommandUseCase;
import com.sashimi.category.application.usecase.CategoryQueryUseCase;
import com.sashimi.category.presentation.api.request.CreateCategoryRequest;
import com.sashimi.category.presentation.api.request.UpdateCategoryRequest;
import com.sashimi.category.presentation.api.response.AdminCategoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryQueryUseCase categoryQueryUseCase;
    private final CategoryCommandUseCase categoryCommandUseCase;

    public AdminCategoryController(CategoryQueryUseCase categoryQueryUseCase,
                                  CategoryCommandUseCase categoryCommandUseCase) {
        this.categoryQueryUseCase = categoryQueryUseCase;
        this.categoryCommandUseCase = categoryCommandUseCase;
    }

    @GetMapping
    public ResponseEntity<List<AdminCategoryResponse>> getCategories() {
        List<AdminCategoryResponse> response = categoryQueryUseCase.getAllCategories()
                .stream().map(AdminCategoryResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody @Valid CreateCategoryRequest request) {
        categoryCommandUseCase.createCategory(new CreateCategoryCommand(request.name(), request.subCategory()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long categoryId,
                                               @RequestBody @Valid UpdateCategoryRequest request) {
        categoryCommandUseCase.updateCategory(new UpdateCategoryCommand(categoryId, request.subCategory()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deactivateCategory(@PathVariable Long categoryId) {
        categoryCommandUseCase.deactivateCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
