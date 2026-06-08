package com.sashimi.recommendation.presentation.api;

import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.recommendation.application.command.CreateJobPostingRecommendationCommand;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationCommandUseCase;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationQueryUseCase;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.presentation.api.request.CreateJobPostingRecommendationRequest;
import com.sashimi.recommendation.presentation.api.response.JobPostingRecommendationResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Job Posting Recommendation", description = "채용공고 기반 AI 추천 API")
@RestController
@RequestMapping("/recommendations/job-posting")
public class JobPostingRecommendationController {

    private final JobPostingRecommendationCommandUseCase commandUseCase;
    private final JobPostingRecommendationQueryUseCase queryUseCase;

    public JobPostingRecommendationController(
            JobPostingRecommendationCommandUseCase commandUseCase,
            JobPostingRecommendationQueryUseCase queryUseCase
    ) {
        this.commandUseCase = commandUseCase;
        this.queryUseCase = queryUseCase;
    }

    @Operation(
            summary = "채용공고 기반 추천 요청",
            description = "채용공고 내용을 저장하고 AI 분석을 비동기로 요청합니다. 응답은 PENDING 상태로 반환되며, 분석 결과는 단건 조회 API로 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "채용공고 기반 추천 요청 접수 성공",
                    content = @Content(schema = @Schema(implementation = JobPostingRecommendationResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 입력값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JobPostingRecommendationResponse create(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody CreateJobPostingRecommendationRequest request
    ) {
        JobPostingRecommendation recommendation = commandUseCase.create(
                new CreateJobPostingRecommendationCommand(
                        principal.getId(),
                        request.inputType(),
                        request.sourceUrl(),
                        request.rawContent()
                )
        );

        return JobPostingRecommendationResponse.from(recommendation);
    }

    @Operation(
            summary = "채용공고 추천 결과 조회",
            description = "채용공고 추천 ID로 AI 분석 상태와 결과를 조회합니다. PENDING이면 분석 중이며, COMPLETED이면 요구 역량, 추천 자격증, 추천 강의가 함께 반환됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채용공고 추천 결과 조회 성공",
                    content = @Content(schema = @Schema(implementation = JobPostingRecommendationResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "채용공고 추천 결과를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{recommendationId}")
    public JobPostingRecommendationResponse getById(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long recommendationId
    ) {
        JobPostingRecommendation recommendation = queryUseCase.getById(
                principal.getId(),
                recommendationId
        );

        return JobPostingRecommendationResponse.from(recommendation);
    }
}