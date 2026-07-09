package com.sashimi.course.presentation.api;

import com.sashimi.course.application.command.*;
import com.sashimi.course.application.usecase.CourseCommandUseCase;
import com.sashimi.course.application.usecase.CourseQueryUseCase;
import com.sashimi.course.application.usecase.InstructorDashboardQueryUseCase;
import com.sashimi.course.presentation.api.request.CreateCourseRequest;
import com.sashimi.course.presentation.api.request.UpdateCourseRequest;
import com.sashimi.course.presentation.api.response.ApprovedCourseResponse;
import com.sashimi.course.presentation.api.response.CourseResponse;
import com.sashimi.course.presentation.api.response.InstructorCourseDetailResponse;
import com.sashimi.course.presentation.api.response.InstructorSalesDashboardResponse;
import com.sashimi.course.presentation.api.response.InstructorStudentDashboardResponse;
import com.sashimi.course.presentation.api.response.InstructorCompletionDashboardResponse;
import com.sashimi.global.storage.FileStoragePort;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
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
    private final InstructorDashboardQueryUseCase instructorDashboardQueryUseCase;

    public InstructorCourseController(CourseCommandUseCase courseCommandUseCase,
                                       CourseQueryUseCase courseQueryUseCase,
                                       FileStoragePort fileStoragePort,
                                       InstructorDashboardQueryUseCase instructorDashboardQueryUseCase) {
        this.courseCommandUseCase = courseCommandUseCase;
        this.courseQueryUseCase = courseQueryUseCase;
        this.fileStoragePort = fileStoragePort;
        this.instructorDashboardQueryUseCase = instructorDashboardQueryUseCase;
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

    @Operation(
            summary = "강사 월별 매출 대시보드 조회",
            description = "로그인한 강사의 이번달 강의 매출, 플랫폼 수수료, 정산 금액, 강의별 매출을 조회합니다."
    )
    @GetMapping("/dashboard/sales")
    public ResponseEntity<InstructorSalesDashboardResponse> getSalesDashboard(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(
                InstructorSalesDashboardResponse.from(
                        instructorDashboardQueryUseCase.getMonthlySales(principal.getId(), year, month)
                )
        );
    }

    @Operation(
            summary = "강사 강좌별 수강생 수 대시보드 조회",
            description = "로그인한 강사의 승인·비공개 강의별 수강생 수와 전체 수강생 수를 조회합니다."
    )
    @GetMapping("/dashboard/students")
    public ResponseEntity<InstructorStudentDashboardResponse> getStudentDashboard(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                InstructorStudentDashboardResponse.from(
                        instructorDashboardQueryUseCase.getStudentCounts(principal.getId())
                )
        );
    }

    @Operation(
            summary = "강사 강좌별 완강률 대시보드 조회",
            description = "로그인한 강사의 승인·비공개 강의별 전체 수강생 수, 완강생 수, 완강률(%)을 조회합니다."
    )
    @GetMapping("/dashboard/completion-rate")
    public ResponseEntity<InstructorCompletionDashboardResponse> getCompletionDashboard(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                InstructorCompletionDashboardResponse.from(
                        instructorDashboardQueryUseCase.getCompletionRates(principal.getId())
                )
        );
    }
}
