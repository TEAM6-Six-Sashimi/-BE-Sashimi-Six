package com.sashimi.course.presentation.api;

import com.sashimi.course.application.usecase.PublicCourseQueryUseCase;
import com.sashimi.course.presentation.api.response.PublicCourseDetailResponse;
import com.sashimi.course.presentation.api.response.PublicCourseResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{courseId}")
    public ResponseEntity<PublicCourseDetailResponse> getCourseDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long courseId) {
        Long userId = principal == null ? null : principal.getId();
        boolean isAdmin = principal != null && principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        return ResponseEntity.ok(PublicCourseDetailResponse.from(
                publicCourseQueryUseCase.getCourseDetail(courseId, userId, isAdmin)
        ));
    }
}