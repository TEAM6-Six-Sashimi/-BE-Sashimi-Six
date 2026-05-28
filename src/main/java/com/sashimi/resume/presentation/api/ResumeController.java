package com.sashimi.resume.presentation.api;

import com.sashimi.resume.application.command.CreateResumeCommand;
import com.sashimi.resume.application.command.DeleteResumeCommand;
import com.sashimi.resume.application.command.ReviewResumeCommand;
import com.sashimi.resume.application.command.UpdateResumeCommand;
import com.sashimi.resume.application.usecase.ResumeCommandUseCase;
import com.sashimi.resume.application.usecase.ResumeQueryUseCase;
import com.sashimi.resume.application.usecase.ReviewResumeUseCase;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeEvaluation;
import com.sashimi.resume.presentation.api.request.CreateResumeRequest;
import com.sashimi.resume.presentation.api.request.ReviewResumeRequest;
import com.sashimi.resume.presentation.api.request.UpdateResumeRequest;
import com.sashimi.resume.presentation.api.response.ResumeResponse;
import com.sashimi.resume.presentation.api.response.ReviewResumeResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "이력서 관리", description = "이력서 작성, 조회, 수정, 삭제 및 AI 평가 API")
@RestController
@RequestMapping("/resumes")
public class ResumeController {

    private final ReviewResumeUseCase reviewResumeUseCase;
    private final ResumeCommandUseCase resumeCommandUseCase;
    private final ResumeQueryUseCase resumeQueryUseCase;

    public ResumeController(
            ResumeCommandUseCase resumeCommandUseCase,
            ResumeQueryUseCase resumeQueryUseCase,
            ReviewResumeUseCase reviewResumeUseCase
    ) {
        this.resumeCommandUseCase = resumeCommandUseCase;
        this.resumeQueryUseCase = resumeQueryUseCase;
        this.reviewResumeUseCase = reviewResumeUseCase;
    }

    @Operation(
            summary = "AI 이력서 평가",
            description = "사용자가 작성한 이력서를 AI가 평가하고 개선점을 제공합니다."
    )
    @PostMapping("/{resumeId}/ai-review")
    public ReviewResumeResponse review(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long resumeId,
            @RequestBody(required = false) ReviewResumeRequest request
    ) {
        Long userId = principal.getId();
        Long jobPostingId = request == null ? null : request.jobPostingId();

        ResumeEvaluation evaluation = reviewResumeUseCase.review(
                new ReviewResumeCommand(userId, resumeId, jobPostingId)
        );

        return ReviewResumeResponse.from(evaluation);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeResponse create(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody CreateResumeRequest request
    ) {
        Resume resume = resumeCommandUseCase.create(
                new CreateResumeCommand(
                        principal.getId(),
                        request.title(),
                        request.content(),
                        Boolean.TRUE.equals(request.defaultResume())
                )
        );

        return ResumeResponse.from(resume);
    }

    @GetMapping
    public List<ResumeResponse> getMyResumes(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return resumeQueryUseCase.getMyResumes(principal.getId()).stream()
                .map(ResumeResponse::from)
                .toList();
    }

    @GetMapping("/{resumeId}")
    public ResumeResponse getResume(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long resumeId
    ) {
        Resume resume = resumeQueryUseCase.getResume(principal.getId(), resumeId);
        return ResumeResponse.from(resume);
    }

    @PatchMapping("/{resumeId}")
    public ResumeResponse update(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long resumeId,
            @RequestBody UpdateResumeRequest request
    ) {
        Resume resume = resumeCommandUseCase.update(
                new UpdateResumeCommand(
                        principal.getId(),
                        resumeId,
                        request.title(),
                        request.content(),
                        request.defaultResume()
                )
        );

        return ResumeResponse.from(resume);
    }

    @DeleteMapping("/{resumeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long resumeId
    ) {
        resumeCommandUseCase.delete(
                new DeleteResumeCommand(principal.getId(), resumeId)
        );
    }

}
