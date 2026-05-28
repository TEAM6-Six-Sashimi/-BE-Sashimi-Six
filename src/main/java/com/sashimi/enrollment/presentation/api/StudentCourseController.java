package com.sashimi.enrollment.presentation.api;

import com.sashimi.enrollment.application.usecase.StudentCourseQueryUseCase;
import com.sashimi.enrollment.presentation.api.response.EnrolledCourseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestHeader("X-USER-ID") Long userId) {
        List<EnrolledCourseResponse> response = studentCourseQueryUseCase.getEnrolledCourses(userId)
                .stream().map(EnrolledCourseResponse::from).toList();
        return ResponseEntity.ok(response);
    }
}