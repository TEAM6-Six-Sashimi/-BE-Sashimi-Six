package com.sashimi.recommendation.presentation.api;

import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationCommandUseCase;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationQueryUseCase;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.presentation.api.request.CreateJobPostingRecommendationRequest;
import com.sashimi.recommendation.presentation.api.response.JobPostingRecommendationResponse;
import com.sashimi.recommendation.presentation.api.response.LatestJobPostingRecommendationResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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

@Tag(name = "채용공고 AI 추천", description = "채용공고 기반 AI 추천 API")
@RestController
@RequestMapping("/recommendations/job-posting")
public class JobPostingRecommendationController {

    private final JobPostingRecommendationCommandUseCase commandUseCase;
    private final JobPostingRecommendationQueryUseCase queryUseCase;
    private final Counter resultViewedCounter;

    public JobPostingRecommendationController(
            JobPostingRecommendationCommandUseCase commandUseCase,
            JobPostingRecommendationQueryUseCase queryUseCase,
            MeterRegistry meterRegistry
    ) {
        this.commandUseCase = commandUseCase;
        this.queryUseCase = queryUseCase;
        this.resultViewedCounter = Counter.builder("ai.recommendation.result.viewed")
                .description("채용공고 AI 추천 결과 조회 수")
                .register(meterRegistry);
    }

    @Operation(
            summary = "채용공고 기반 추천 요청",
            description = "채용공고 URL 또는 텍스트를 입력받아 AI 분석을 비동기로 요청합니다. resumeId가 있으면 해당 이력서와 비교 분석합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "채용공고 기반 추천 요청 접수 성공",
                    content = @Content(schema = @Schema(implementation = JobPostingRecommendationResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 입력값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음",
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
                request.toCommand(principal.getId())
        );

        return JobPostingRecommendationResponse.from(recommendation);
    }

    @Operation(
            summary = "최근 채용공고 추천 결과 조회",
            description = "사용자의 가장 최근 채용공고 추천 결과 1개를 조회합니다. 최근 기록이 없으면 recommendation을 null로 반환합니다."
    )
    @GetMapping("/latest")
    public LatestJobPostingRecommendationResponse getLatest(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return queryUseCase.getLatest(principal.getId())
                .map(LatestJobPostingRecommendationResponse::of)
                .orElseGet(LatestJobPostingRecommendationResponse::empty);
    }

    @Operation(
            summary = "채용공고 추천 결과 조회",
            description = "채용공고 추천 ID로 AI 분석 상태와 결과를 조회합니다."
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

        resultViewedCounter.increment();
        return JobPostingRecommendationResponse.from(recommendation);
    }
}