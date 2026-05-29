package com.sashimi.recommendation.presentation.api;

import com.sashimi.recommendation.application.command.CreateJobPostingRecommendationCommand;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationCommandUseCase;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationQueryUseCase;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.presentation.api.request.CreateJobPostingRecommendationRequest;
import com.sashimi.recommendation.presentation.api.response.CertificateRecommendationResponse;
import com.sashimi.recommendation.presentation.api.response.CourseRecommendationResponse;
import com.sashimi.recommendation.presentation.api.response.JobPostingRecommendationResponse;
import com.sashimi.recommendation.presentation.api.response.RequiredSkillRecommendationResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sashimi.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

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
            summary = "채용공고 기반 추천 생성",
            description = "채용공고 URL 또는 텍스트를 기반으로 AI 분석을 수행하고 추천 요약 결과를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "채용공고 기반 추천 생성 성공",
                    content = @Content(schema = @Schema(implementation = JobPostingRecommendationResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 입력값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "활성화된 AI 프롬프트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "AI API Key 설정 오류 또는 서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "AI API 호출 실패 또는 AI 응답 파싱 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
            summary = "최신 채용공고 추천 조회",
            description = "로그인한 사용자의 가장 최근 채용공고 기반 추천 요약 결과를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최신 추천 결과 조회 성공",
                    content = @Content(schema = @Schema(implementation = JobPostingRecommendationResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "채용공고 추천 결과를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public JobPostingRecommendationResponse getLatest(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        JobPostingRecommendation recommendation = queryUseCase.getLatest(principal.getId());
        return JobPostingRecommendationResponse.from(recommendation);
    }

    @Operation(
            summary = "채용공고 요구 역량 조회",
            description = "가장 최근 채용공고 추천 결과에서 추출된 요구 역량 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요구 역량 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RequiredSkillRecommendationResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "채용공고 추천 결과를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/skills")
    public List<RequiredSkillRecommendationResponse> getSkills(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return queryUseCase.getLatestSkills(principal.getId()).stream()
                .map(RequiredSkillRecommendationResponse::from)
                .toList();
    }

    @Operation(
            summary = "채용공고 기반 추천 강의 조회",
            description = "가장 최근 채용공고 추천 결과를 기반으로 추천 강의 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 강의 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourseRecommendationResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "채용공고 추천 결과를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/courses")
    public List<CourseRecommendationResponse> recommendCourses(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return queryUseCase.getLatestCourses(principal.getId()).stream()
                .map(CourseRecommendationResponse::from)
                .toList();
    }

    @Operation(
            summary = "채용공고 기반 추천 자격증 조회",
            description = "가장 최근 채용공고 추천 결과를 기반으로 추천 자격증 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 자격증 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CertificateRecommendationResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "채용공고 추천 결과를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/certificates")
    public List<CertificateRecommendationResponse> getCertificates(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return queryUseCase.getLatestCertificates(principal.getId()).stream()
                .map(CertificateRecommendationResponse::from)
                .toList();
    }
}
