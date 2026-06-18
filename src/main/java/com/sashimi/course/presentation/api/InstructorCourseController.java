package com.sashimi.course.presentation.api;

import com.sashimi.course.application.command.*;
import com.sashimi.course.application.usecase.CourseCommandUseCase;
import com.sashimi.course.application.usecase.CourseQueryUseCase;
import com.sashimi.course.presentation.api.request.CreateCourseRequest;
import com.sashimi.course.presentation.api.request.UpdateCourseRequest;
import com.sashimi.course.presentation.api.response.ApprovedCourseResponse;
import com.sashimi.course.presentation.api.response.CourseResponse;
import com.sashimi.course.presentation.api.response.InstructorCourseDetailResponse;
import com.sashimi.member.presentation.api.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructor/courses")
public class InstructorCourseController {

    private final CourseCommandUseCase courseCommandUseCase;
    private final CourseQueryUseCase courseQueryUseCase;

    public InstructorCourseController(CourseCommandUseCase courseCommandUseCase,
                                       CourseQueryUseCase courseQueryUseCase) {
        this.courseCommandUseCase = courseCommandUseCase;
        this.courseQueryUseCase = courseQueryUseCase;
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ApprovedCourseResponse>> getApprovedCourses(
            @RequestHeader("X-USER-ID") Long instructorId) {
        List<ApprovedCourseResponse> courses = courseQueryUseCase.getApprovedCoursesByInstructor(instructorId)
                .stream().map(ApprovedCourseResponse::from).toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<CourseResponse>> getInProgressCourses(
            @RequestHeader("X-USER-ID") Long instructorId) {
        List<CourseResponse> courses = courseQueryUseCase.getInProgressCoursesByInstructor(instructorId)
                .stream().map(CourseResponse::from).toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<InstructorCourseDetailResponse> getCourseDetail(
            @RequestHeader("X-USER-ID") Long instructorId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(InstructorCourseDetailResponse.from(courseQueryUseCase.getCourseDetail(courseId, instructorId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createCourse(
            @RequestHeader("X-USER-ID") Long instructorId,
            @RequestBody CreateCourseRequest request) {
        List<CreateSessionCommand> sessionCommands = request.sessions().stream()
                .map(s -> new CreateSessionCommand(s.title(), s.videoUrl(), 0, 0, s.preview(), null, null, null, null))
                .toList();

        Long courseId = courseCommandUseCase.createCourse(new CreateCourseCommand(
                instructorId, request.subCategoryName(), request.title(), request.description(),
                request.price(), request.difficulty(), request.thumbnail(),
                request.ncsInfoId(), request.initialStatus(), sessionCommands));

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("성공했습니다."));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Void> updateCourse(
            @RequestHeader("X-USER-ID") Long instructorId,
            @PathVariable Long courseId,
            @RequestBody UpdateCourseRequest request) {
        List<CreateSessionCommand> sessionCommands = request.sessions().stream()
                .map(s -> new CreateSessionCommand(s.title(), s.videoUrl(), 0, 0, s.preview(), null, null, null, null))
                .toList();

        courseCommandUseCase.updateCourse(new UpdateCourseCommand(
                courseId, instructorId, request.categoryId(), request.title(), request.description(),
                request.price(), request.difficulty(), request.thumbnail(),
                request.ncsInfoId(), request.targetStatus(), sessionCommands));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @RequestHeader("X-USER-ID") Long instructorId,
            @PathVariable Long courseId) {
        courseCommandUseCase.deleteCourse(new DeleteCourseCommand(courseId, instructorId));
        return ResponseEntity.noContent().build();
    }
}
