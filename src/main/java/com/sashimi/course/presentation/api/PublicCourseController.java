package com.sashimi.course.presentation.api;

import com.sashimi.course.application.usecase.PublicCourseQueryUseCase;
import com.sashimi.course.presentation.api.response.PublicCourseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class PublicCourseController {

    private final PublicCourseQueryUseCase publicCourseQueryUseCase;

    public PublicCourseController(PublicCourseQueryUseCase publicCourseQueryUseCase) {
        this.publicCourseQueryUseCase = publicCourseQueryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<PublicCourseResponse>> getCourses(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categoryName
    ) {
        List<PublicCourseResponse> response;

        if (categoryId != null) {
            response = publicCourseQueryUseCase.getCoursesBySubCategory(categoryId)
                    .stream().map(PublicCourseResponse::from).toList();
        } else if (categoryName != null) {
            response = publicCourseQueryUseCase.getCoursesByCategory(categoryName)
                    .stream().map(PublicCourseResponse::from).toList();
        } else {
            response = publicCourseQueryUseCase.getAllApprovedCourses()
                    .stream().map(PublicCourseResponse::from).toList();
        }

        return ResponseEntity.ok(response);
    }
}