package com.sashimi.enrollment.presentation.api;

import com.sashimi.enrollment.application.usecase.StudentCourseQueryUseCase;
import com.sashimi.enrollment.presentation.api.response.EnrolledCourseDetailResponse;
import com.sashimi.enrollment.presentation.api.response.EnrolledCourseResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/courses")
public class StudentCourseController {

    private final StudentCourseQueryUseCase studentCourseQueryUseCase;

    public StudentCourseController(StudentCourseQueryUseCase studentCourseQueryUseCase) {
        this.studentCourseQueryUseCase = studentCourseQueryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<EnrolledCourseResponse>> getEnrolledCourses(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<EnrolledCourseResponse> response = studentCourseQueryUseCase.getEnrolledCourses(principal.getId())
                .stream().map(EnrolledCourseResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<EnrolledCourseDetailResponse> getEnrolledCourseDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(EnrolledCourseDetailResponse.from(
                studentCourseQueryUseCase.getEnrolledCourseDetail(principal.getId(), courseId)
        ));
    }
}