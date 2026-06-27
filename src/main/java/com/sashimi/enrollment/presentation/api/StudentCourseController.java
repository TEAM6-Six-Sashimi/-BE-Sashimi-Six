package com.sashimi.enrollment.presentation.api;

import com.sashimi.enrollment.application.usecase.LearningProgressCommandUseCase;
import com.sashimi.enrollment.application.usecase.StudentCourseQueryUseCase;
import com.sashimi.enrollment.presentation.api.request.ReportProgressRequest;
import com.sashimi.enrollment.presentation.api.response.EnrolledCourseResponse;
import com.sashimi.enrollment.presentation.api.response.ProgressResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/courses")
public class StudentCourseController {

    private final StudentCourseQueryUseCase studentCourseQueryUseCase;
    private final LearningProgressCommandUseCase learningProgressCommandUseCase;

    public StudentCourseController(StudentCourseQueryUseCase studentCourseQueryUseCase,
                                   LearningProgressCommandUseCase learningProgressCommandUseCase) {
        this.studentCourseQueryUseCase = studentCourseQueryUseCase;
        this.learningProgressCommandUseCase = learningProgressCommandUseCase;
    }

    @GetMapping
    public ResponseEntity<List<EnrolledCourseResponse>> getEnrolledCourses(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<EnrolledCourseResponse> response = studentCourseQueryUseCase.getEnrolledCourses(principal.getId())
                .stream().map(EnrolledCourseResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/sessions/{sessionId}/progress")
    public ResponseEntity<ProgressResponse> reportProgress(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long courseId,
            @PathVariable Long sessionId,
            @RequestBody ReportProgressRequest request) {
        ProgressResponse response = ProgressResponse.from(
                learningProgressCommandUseCase.reportProgress(
                        principal.getId(), courseId, sessionId, request.lastPositionSeconds())
        );
        return ResponseEntity.ok(response);
    }
}