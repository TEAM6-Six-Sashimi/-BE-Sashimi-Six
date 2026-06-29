package com.sashimi.course.presentation.api;

import com.sashimi.course.application.command.*;
import com.sashimi.course.application.usecase.CourseCommandUseCase;
import com.sashimi.course.application.usecase.CourseQueryUseCase;
import com.sashimi.course.presentation.api.request.CreateCourseRequest;
import com.sashimi.course.presentation.api.request.UpdateCourseRequest;
import com.sashimi.course.presentation.api.response.ApprovedCourseResponse;
import com.sashimi.course.presentation.api.response.CourseResponse;
import com.sashimi.course.presentation.api.response.InstructorCourseDetailResponse;
import com.sashimi.global.storage.FileStoragePort;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/instructor/courses")
public class InstructorCourseController {

    private final CourseCommandUseCase courseCommandUseCase;
    private final CourseQueryUseCase courseQueryUseCase;
    private final FileStoragePort fileStoragePort;

    public InstructorCourseController(CourseCommandUseCase courseCommandUseCase,
                                       CourseQueryUseCase courseQueryUseCase,
                                       FileStoragePort fileStoragePort) {
        this.courseCommandUseCase = courseCommandUseCase;
        this.courseQueryUseCase = courseQueryUseCase;
        this.fileStoragePort = fileStoragePort;
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ApprovedCourseResponse>> getApprovedCourses(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<ApprovedCourseResponse> courses = courseQueryUseCase.getApprovedCoursesByInstructor(principal.getId())
                .stream().map(ApprovedCourseResponse::from).toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<CourseResponse>> getInProgressCourses(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<CourseResponse> courses = courseQueryUseCase.getInProgressCoursesByInstructor(principal.getId())
                .stream().map(CourseResponse::from).toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/closed")
    public ResponseEntity<List<ApprovedCourseResponse>> getClosedCourses(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<ApprovedCourseResponse> courses = courseQueryUseCase.getClosedCoursesByInstructor(principal.getId())
                .stream().map(ApprovedCourseResponse::from).toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<InstructorCourseDetailResponse> getCourseDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(InstructorCourseDetailResponse.from(
                courseQueryUseCase.getCourseDetail(courseId, principal.getId()), fileStoragePort));
    }

    @PostMapping
    public ResponseEntity<Void> createCourse(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody CreateCourseRequest request) {
        var requestSessions = request.sessions();
        List<CreateSessionCommand> sessionCommands = IntStream.range(0, requestSessions.size())
                .mapToObj(i -> {
                    var s = requestSessions.get(i);
                    return new CreateSessionCommand(s.title(), s.videoUrl(), s.durationSeconds(), i + 1, s.preview(),
                            s.attachmentName(), s.attachmentUrl(), s.attachmentType(), s.attachmentSize());
                })
                .toList();

        Long courseId = courseCommandUseCase.createCourse(new CreateCourseCommand(
                principal.getId(), request.subCategoryName(), request.title(), request.description(),
                request.price(), request.difficulty(), request.thumbnail(),
                request.initialStatus(), sessionCommands));

        return ResponseEntity.created(URI.create("/instructor/courses/" + courseId)).build();
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Void> updateCourse(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long courseId,
            @RequestBody UpdateCourseRequest request) {
        var requestSessions = request.sessions();
        List<CreateSessionCommand> sessionCommands = IntStream.range(0, requestSessions.size())
                .mapToObj(i -> {
                    var s = requestSessions.get(i);
                    return new CreateSessionCommand(s.title(), s.videoUrl(), s.durationSeconds(), i + 1, s.preview(),
                            s.attachmentName(), s.attachmentUrl(), s.attachmentType(), s.attachmentSize());
                })
                .toList();

        courseCommandUseCase.updateCourse(new UpdateCourseCommand(
                courseId, principal.getId(), request.categoryId(), request.title(), request.description(),
                request.price(), request.difficulty(), request.thumbnail(),
                request.targetStatus(), sessionCommands));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long courseId) {
        courseCommandUseCase.deleteCourse(new DeleteCourseCommand(courseId, principal.getId()));
        return ResponseEntity.noContent().build();
    }
}
