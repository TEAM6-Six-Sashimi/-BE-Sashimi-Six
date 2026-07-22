package com.sashimi.resume.presentation.api;

import com.sashimi.resume.application.command.CreateResumeCommand;
import com.sashimi.resume.application.command.DeleteResumeCommand;
import com.sashimi.resume.application.command.UpdateResumeCommand;
import com.sashimi.resume.application.result.ReviewResumeResult;
import com.sashimi.resume.application.service.ResumeReviewService;
import com.sashimi.resume.application.usecase.ResumeCommandUseCase;
import com.sashimi.resume.application.usecase.ResumeQueryUseCase;
import com.sashimi.resume.application.usecase.ReviewResumeUseCase;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.presentation.api.request.CreateResumeRequest;
import com.sashimi.resume.presentation.api.request.UpdateResumeRequest;
import com.sashimi.resume.presentation.api.response.LatestResumeReviewResponse;
import com.sashimi.resume.presentation.api.response.ResumeResponse;
import com.sashimi.resume.presentation.api.response.ReviewResumeResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import com.sashimi.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이력서 관리", description = "이력서 작성, 조회, 수정, 삭제 및 AI 평가 API")
@RestController
@RequestMapping("/resumes")
public class ResumeController {

    private final ReviewResumeUseCase reviewResumeUseCase;
    private final ResumeReviewService resumeReviewService;
    private final ResumeCommandUseCase resumeCommandUseCase;
    private final ResumeQueryUseCase resumeQueryUseCase;

    public ResumeController(
            ResumeCommandUseCase resumeCommandUseCase,
            ResumeQueryUseCase resumeQueryUseCase,
            ReviewResumeUseCase reviewResumeUseCase,
            ResumeReviewService resumeReviewService
    ) {
        this.resumeCommandUseCase = resumeCommandUseCase;
        this.resumeQueryUseCase = resumeQueryUseCase;
        this.reviewResumeUseCase = reviewResumeUseCase;
        this.resumeReviewService = resumeReviewService;
    }

    @Operation(
            summary = "AI 이력서 평가",
            description = "로그인한 사용자가 작성한 이력서를 AI가 평가하고 강점, 약점, 개선 제안을 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI 이력서 평가 성공",
                    content = @Content(schema = @Schema(implementation = ReviewResumeResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 입력값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "이력서 또는 활성화된 AI 프롬프트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "AI API Key 설정 오류 또는 서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "AI API 호출 실패 또는 AI 응답 파싱 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{resumeId}/ai-review")
    public ReviewResumeResponse review(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long resumeId
    ) {
        ReviewResumeResult result = reviewResumeUseCase.review(
                resumeId,
                principal.getId()
        );

        return ReviewResumeResponse.from(result);
    }

    @Operation(
            summary = "최근 이력서 AI 평가 결과 조회",
            description = "사용자의 특정 이력서에 대한 최근 AI 평가 결과 1개를 조회합니다. 최근 기록이 없으면 review를 null로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최근 이력서 AI 평가 결과 조회 성공",
                    content = @Content(schema = @Schema(implementation = LatestResumeReviewResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{resumeId}/ai-review/latest")
    public LatestResumeReviewResponse getLatestReview(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long resumeId
    ) {
        return resumeReviewService.getLatestReview(
                        resumeId,
                        principal.getId()
                )
                .map(result -> LatestResumeReviewResponse.of(
                        resumeId,
                        result
                ))
                .orElseGet(() -> LatestResumeReviewResponse.empty(
                        resumeId
                ));
    }

    @Operation(
            summary = "이력서 작성",
            description = "로그인한 사용자가 이력서를 작성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "이력서 작성 성공",
                    content = @Content(schema = @Schema(implementation = ResumeResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 입력값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 작성된 이력서가 있음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeResponse create(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody CreateResumeRequest request
    ) {
        Resume resume = resumeCommandUseCase.create(
                new CreateResumeCommand(
                        principal.getId(),
                        request.toEducations(),
                        request.entryLevel(),
                        request.toCareers(),
                        request.toCertifications(),
                        Boolean.TRUE.equals(request.defaultResume())
                )
        );

        return ResumeResponse.from(resume);
    }

    @Operation(
            summary = "내 이력서 조회",
            description = "로그인한 사용자가 작성한 이력서를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이력서 조회 성공",
                    content = @Content(schema = @Schema(implementation = ResumeResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResumeResponse getMyResume(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Resume resume = resumeQueryUseCase.getMyResume(
                principal.getId()
        );

        return ResumeResponse.from(resume);
    }

    @Operation(
            summary = "이력서 수정",
            description = "로그인한 사용자가 작성한 이력서를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이력서 수정 성공",
                    content = @Content(schema = @Schema(implementation = ResumeResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 입력값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{resumeId}")
    public ResumeResponse update(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long resumeId,
            @RequestBody UpdateResumeRequest request
    ) {
        Resume resume = resumeCommandUseCase.update(
                new UpdateResumeCommand(
                        principal.getId(),
                        resumeId,
                        request.toEducations(),
                        request.entryLevel(),
                        request.toCareers(),
                        request.toCertifications(),
                        request.defaultResume()
                )
        );

        return ResumeResponse.from(resume);
    }

    @Operation(
            summary = "이력서 삭제",
            description = "로그인한 사용자가 작성한 이력서를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "이력서 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{resumeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long resumeId
    ) {
        resumeCommandUseCase.delete(
                new DeleteResumeCommand(principal.getId(), resumeId)
        );
    }
}