package com.sashimi.resume.presentation.api;

import com.sashimi.resume.application.command.ReviewResumeCommand;
import com.sashimi.resume.application.usecase.ReviewResumeUseCase;
import com.sashimi.resume.domain.model.ResumeEvaluation;
import com.sashimi.resume.presentation.api.request.ReviewResumeRequest;
import com.sashimi.resume.presentation.api.response.ReviewResumeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이력서 관리", description = "이력서 작성, 조회, 수정, 삭제 및 AI 평가 API")
@RestController
@RequestMapping("/resumes")
public class ResumeController {

    private final ReviewResumeUseCase reviewResumeUseCase;

    public ResumeController(ReviewResumeUseCase reviewResumeUseCase) {
        this.reviewResumeUseCase = reviewResumeUseCase;
    }

    @Operation(
            summary = "AI 이력서 평가",
            description = "사용자가 작성한 이력서를 AI가 평가하고 개선점을 제공합니다."
    )
    @PostMapping("/{resumeId}/ai-review")
    public ReviewResumeResponse review(
            @PathVariable Long resumeId,
            @RequestBody(required = false) ReviewResumeRequest request
    ) {
        // TODO: 인증/인가 구현 후 SecurityContext에서 userId를 꺼내도록 변경한다.
        Long userId = 1L;

        Long jobPostingId = request == null ? null : request.jobPostingId();

        ResumeEvaluation evaluation = reviewResumeUseCase.review(
                new ReviewResumeCommand(userId, resumeId, jobPostingId)
        );

        return ReviewResumeResponse.from(evaluation);
    }
}
