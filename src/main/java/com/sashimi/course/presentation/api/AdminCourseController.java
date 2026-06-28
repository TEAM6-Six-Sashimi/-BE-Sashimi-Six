package com.sashimi.course.presentation.api;

import com.sashimi.course.application.command.ApproveCourseCommand;
import com.sashimi.course.application.command.RejectCourseCommand;
import com.sashimi.course.application.port.CategoryPort;
import com.sashimi.course.application.port.InstructorPort;
import com.sashimi.course.application.usecase.CourseCommandUseCase;
import com.sashimi.course.application.usecase.CourseQueryUseCase;
import com.sashimi.course.presentation.api.request.RejectCourseRequest;
import com.sashimi.course.presentation.api.response.AdminCourseListResponse;
import com.sashimi.course.presentation.api.response.AdminCoursePendingResponse;
import com.sashimi.course.presentation.api.response.AdminCourseRejectedResponse;
import com.sashimi.course.presentation.api.response.CourseResponse;
import com.sashimi.course.presentation.api.response.RejectReasonResponse;
import com.sashimi.course.domain.model.RejectReasonCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private final CourseCommandUseCase courseCommandUseCase;
    private final CourseQueryUseCase courseQueryUseCase;
    private final CategoryPort categoryPort;
    private final InstructorPort instructorPort;

    public AdminCourseController(CourseCommandUseCase courseCommandUseCase,
                                  CourseQueryUseCase courseQueryUseCase,
                                  CategoryPort categoryPort,
                                  InstructorPort instructorPort) {
        this.courseCommandUseCase = courseCommandUseCase;
        this.courseQueryUseCase = courseQueryUseCase;
        this.categoryPort = categoryPort;
        this.instructorPort = instructorPort;
    }

    @GetMapping
    public ResponseEntity<List<AdminCourseListResponse>> getAllCourses() {
        List<AdminCourseListResponse> courses = courseQueryUseCase.getAllCoursesForAdmin()
                .stream()
                .map(course -> AdminCourseListResponse.of(
                        course,
                        categoryPort.getCategoryNameById(course.getCategoryId()),
                        instructorPort.getInstructorName(course.getInstructorId())
                ))
                .toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/closed")
    public ResponseEntity<List<AdminCourseListResponse>> getClosedCourses() {
        List<AdminCourseListResponse> courses = courseQueryUseCase.getClosedCoursesForAdmin()
                .stream()
                .map(course -> AdminCourseListResponse.of(
                        course,
                        categoryPort.getCategoryNameById(course.getCategoryId()),
                        instructorPort.getInstructorName(course.getInstructorId())
                ))
                .toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AdminCoursePendingResponse>> getPendingCourses() {
        List<AdminCoursePendingResponse> courses = courseQueryUseCase.getPendingCoursesForAdmin()
                .stream()
                .map(course -> AdminCoursePendingResponse.of(
                        course,
                        categoryPort.getCategoryNameById(course.getCategoryId()),
                        instructorPort.getInstructorName(course.getInstructorId()),
                        instructorPort.getInstructorLoginId(course.getInstructorId())
                ))
                .toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<AdminCourseRejectedResponse>> getRejectedCourses() {
        List<AdminCourseRejectedResponse> courses = courseQueryUseCase.getRejectedCoursesForAdmin()
                .stream()
                .map(course -> AdminCourseRejectedResponse.of(
                        course,
                        categoryPort.getCategoryNameById(course.getCategoryId()),
                        instructorPort.getInstructorName(course.getInstructorId())
                ))
                .toList();
        return ResponseEntity.ok(courses);
    }

    @PatchMapping("/{courseId}/approve")
    public ResponseEntity<Void> approveCourse(@PathVariable Long courseId) {
        courseCommandUseCase.approveCourse(new ApproveCourseCommand(courseId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reject-reasons")
    public ResponseEntity<List<RejectReasonResponse>> getRejectReasons() {
        List<RejectReasonResponse> reasons = Arrays.stream(RejectReasonCategory.values())
                .map(RejectReasonResponse::from)
                .toList();
        return ResponseEntity.ok(reasons);
    }

    @PatchMapping("/{courseId}/reject")
    public ResponseEntity<Void> rejectCourse(@PathVariable Long courseId,
                                              @RequestBody RejectCourseRequest request) {
        courseCommandUseCase.rejectCourse(new RejectCourseCommand(courseId, request.category(), request.detail()));
        return ResponseEntity.noContent().build();
    }
}
