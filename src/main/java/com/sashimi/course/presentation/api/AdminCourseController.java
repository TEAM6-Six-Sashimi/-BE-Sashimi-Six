package com.sashimi.course.presentation.api;

import com.sashimi.course.application.command.ApproveCourseCommand;
import com.sashimi.course.application.command.RejectCourseCommand;
import com.sashimi.course.application.usecase.CourseCommandUseCase;
import com.sashimi.course.application.usecase.CourseQueryUseCase;
import com.sashimi.course.presentation.api.request.RejectCourseRequest;
import com.sashimi.course.presentation.api.response.CourseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private final CourseCommandUseCase courseCommandUseCase;
    private final CourseQueryUseCase courseQueryUseCase;

    public AdminCourseController(CourseCommandUseCase courseCommandUseCase,
                                  CourseQueryUseCase courseQueryUseCase) {
        this.courseCommandUseCase = courseCommandUseCase;
        this.courseQueryUseCase = courseQueryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getApprovedCourses() {
        List<CourseResponse> courses = courseQueryUseCase.getApprovedCoursesForAdmin()
                .stream().map(CourseResponse::from).toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CourseResponse>> getPendingCourses() {
        List<CourseResponse> courses = courseQueryUseCase.getPendingCoursesForAdmin()
                .stream().map(CourseResponse::from).toList();
        return ResponseEntity.ok(courses);
    }

    @PatchMapping("/{courseId}/approve")
    public ResponseEntity<Void> approveCourse(@PathVariable Long courseId) {
        courseCommandUseCase.approveCourse(new ApproveCourseCommand(courseId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{courseId}/reject")
    public ResponseEntity<Void> rejectCourse(@PathVariable Long courseId,
                                              @RequestBody RejectCourseRequest request) {
        courseCommandUseCase.rejectCourse(new RejectCourseCommand(courseId, request.rejectReason()));
        return ResponseEntity.noContent().build();
    }
}
