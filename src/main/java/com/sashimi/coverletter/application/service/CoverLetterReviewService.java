package com.sashimi.coverletter.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.domain.model.AiRequestStatus;
import com.sashimi.ai.infrastructure.fastapi.coverletter.FastApiCoverLetterClient;
import com.sashimi.ai.infrastructure.fastapi.coverletter.FastApiCoverLetterReviewResponse;
import com.sashimi.ai.infrastructure.persistence.AiRequestHistoryJpaEntity;
import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.coverletter.domain.model.CoverLetterQuestion;
import com.sashimi.coverletter.presentation.api.response.CoverLetterReviewCreateResponse;
import com.sashimi.coverletter.presentation.api.response.CoverLetterReviewQuestionResponse;
import com.sashimi.coverletter.presentation.api.response.CoverLetterReviewResultResponse;
import com.sashimi.coverletter.presentation.api.response.CoverLetterReviewSummaryResponse;
import com.sashimi.coverletter.presentation.api.response.CoverLetterSpellingCorrectionResponse;
import com.sashimi.coverletter.presentation.api.response.LatestCoverLetterReviewSummaryResponse;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CoverLetterReviewService {

    private static final int TOTAL_QUESTION_COUNT = 5;

    private final SpringDataAiRequestHistoryRepository aiRequestHistoryRepository;
    private final FastApiCoverLetterClient fastApiCoverLetterClient;
    private final ObjectMapper objectMapper;
    private final CoverLetterReviewTransactionService coverLetterReviewTransactionService;

    public CoverLetterReviewService(
            SpringDataAiRequestHistoryRepository aiRequestHistoryRepository,
            FastApiCoverLetterClient fastApiCoverLetterClient,
            ObjectMapper objectMapper,
            CoverLetterReviewTransactionService coverLetterReviewTransactionService
    ) {
        this.aiRequestHistoryRepository = aiRequestHistoryRepository;
        this.fastApiCoverLetterClient = fastApiCoverLetterClient;
        this.objectMapper = objectMapper;
        this.coverLetterReviewTransactionService =
                coverLetterReviewTransactionService;
    }

    public CoverLetterReviewCreateResponse review(Long userId) {
        CoverLetterReviewPreparation preparation =
                coverLetterReviewTransactionService.prepareReview(userId);

        try {
            FastApiCoverLetterReviewResponse fastApiResponse =
                    fastApiCoverLetterClient.review(
                            preparation.fastApiRequest()
                    );

            CoverLetterReviewResultResponse result = toReviewResult(
                    preparation.historyId(),
                    preparation.createdAt(),
                    preparation.contentMap(),
                    fastApiResponse
            );

            coverLetterReviewTransactionService.completeReview(
                    preparation.historyId(),
                    result
            );

            return new CoverLetterReviewCreateResponse(
                    preparation.historyId(),
                    preparation.createdAt(),
                    result.summary()
            );
        } catch (RuntimeException exception) {
            coverLetterReviewTransactionService.failReview(
                    preparation.historyId(),
                    exception
            );

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public LatestCoverLetterReviewSummaryResponse getLatestSummary(Long userId) {
        return aiRequestHistoryRepository
                .findFirstByUserIdAndFeatureTypeAndStatusOrderByCreatedAtDesc(
                        userId,
                        AiFeatureType.COVER_LETTER_REVIEW,
                        AiRequestStatus.COMPLETED
                )
                .map(history -> {
                    CoverLetterReviewResultResponse result =
                            readResult(history.getResultJson());

                    return LatestCoverLetterReviewSummaryResponse.of(
                            history.getId(),
                            history.getCreatedAt(),
                            result.summary()
                    );
                })
                .orElseGet(LatestCoverLetterReviewSummaryResponse::empty);
    }

    @Transactional(readOnly = true)
    public CoverLetterReviewResultResponse getReviewDetail(
            Long userId,
            Long reviewId
    ) {
        AiRequestHistoryJpaEntity history = aiRequestHistoryRepository
                .findByIdAndUserIdAndFeatureTypeAndStatus(
                        reviewId,
                        userId,
                        AiFeatureType.COVER_LETTER_REVIEW,
                        AiRequestStatus.COMPLETED
                )
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.COVER_LETTER_REVIEW_NOT_FOUND
                ));

        if (history.getResultJson() == null || history.getResultJson().isBlank()) {
            throw new BusinessException(
                    ErrorCode.COVER_LETTER_REVIEW_NOT_FOUND
            );
        }

        return readResult(history.getResultJson());
    }

    private CoverLetterReviewResultResponse toReviewResult(
            Long reviewId,
            LocalDateTime createdAt,
            Map<CoverLetterQuestion, String> contentMap,
            FastApiCoverLetterReviewResponse fastApiResponse
    ) {
        Map<String, FastApiCoverLetterReviewResponse.QuestionReview> reviewMap =
                safeList(fastApiResponse.questions()).stream()
                        .collect(Collectors.toMap(
                                FastApiCoverLetterReviewResponse.QuestionReview::questionKey,
                                Function.identity(),
                                (first, second) -> first
                        ));

        List<CoverLetterReviewQuestionResponse> questions =
                Arrays.stream(CoverLetterQuestion.values())
                        .sorted(java.util.Comparator.comparingInt(
                                CoverLetterQuestion::displayOrder
                        ))
                        .map(question -> toQuestionResponse(
                                question,
                                contentMap.getOrDefault(question, ""),
                                reviewMap.get(question.name())
                        ))
                        .toList();

        CoverLetterReviewSummaryResponse summary =
                createSummaryResponse(
                        questions,
                        fastApiResponse
                );

        return new CoverLetterReviewResultResponse(
                reviewId,
                createdAt,
                summary,
                questions
        );
    }

    private CoverLetterReviewQuestionResponse toQuestionResponse(
            CoverLetterQuestion question,
            String content,
            FastApiCoverLetterReviewResponse.QuestionReview review
    ) {
        String normalizedContent = normalize(content);

        if (normalizedContent.isBlank()) {
            return createEmptyQuestionResponse(question);
        }

        if (review == null) {
            return createMissingReviewQuestionResponse(
                    question,
                    normalizedContent
            );
        }

        List<CoverLetterSpellingCorrectionResponse> corrections =
                safeList(review.spellingCorrections()).stream()
                        .map(correction -> new CoverLetterSpellingCorrectionResponse(
                                correction.original(),
                                correction.corrected()
                        ))
                        .toList();

        return new CoverLetterReviewQuestionResponse(
                question.displayOrder(),
                question.name(),
                question.title(),
                question.maxLength(),
                defaultText(review.status(), "RECOMMENDED"),
                defaultText(review.summaryFeedback(), ""),
                corrections.size(),
                review.expressionImprovementCount(),
                review.flowImprovementCount(),
                normalizedContent,
                corrections,
                defaultText(review.feedback(), ""),
                defaultText(review.improvedExample(), "")
        );
    }

    private CoverLetterReviewQuestionResponse createEmptyQuestionResponse(
            CoverLetterQuestion question
    ) {
        return new CoverLetterReviewQuestionResponse(
                question.displayOrder(),
                question.name(),
                question.title(),
                question.maxLength(),
                "EMPTY",
                "작성된 내용이 없습니다.",
                0,
                0,
                0,
                "",
                List.of(),
                "자기소개서 문항을 작성하면 AI 첨삭을 받을 수 있습니다.",
                ""
        );
    }

    private CoverLetterReviewQuestionResponse createMissingReviewQuestionResponse(
            CoverLetterQuestion question,
            String content
    ) {
        return new CoverLetterReviewQuestionResponse(
                question.displayOrder(),
                question.name(),
                question.title(),
                question.maxLength(),
                "NEEDS_REVISION",
                "AI 첨삭 결과를 확인하지 못했습니다.",
                0,
                0,
                0,
                content,
                List.of(),
                "해당 문항의 AI 첨삭 결과를 확인하지 못했습니다. 다시 평가를 요청해 주세요.",
                ""
        );
    }

    private CoverLetterReviewSummaryResponse createSummaryResponse(
            List<CoverLetterReviewQuestionResponse> questions,
            FastApiCoverLetterReviewResponse fastApiResponse
    ) {
        int completedCount = (int) questions.stream()
                .filter(question -> !"EMPTY".equals(question.status()))
                .count();

        int needRevisionCount = (int) questions.stream()
                .filter(question -> "NEEDS_REVISION".equals(question.status()))
                .count();

        int recommendedCount = (int) questions.stream()
                .filter(question -> "RECOMMENDED".equals(question.status()))
                .count();

        int spellingCorrectionCount = questions.stream()
                .mapToInt(CoverLetterReviewQuestionResponse::spellingCorrectionCount)
                .sum();

        return new CoverLetterReviewSummaryResponse(
                completedCount,
                TOTAL_QUESTION_COUNT,
                needRevisionCount,
                recommendedCount,
                spellingCorrectionCount,
                fastApiResponse.repeatedExpressionCount(),
                fastApiResponse.averageSentenceLength(),
                defaultText(
                        fastApiResponse.overallComment(),
                        "자기소개서 첨삭이 완료되었습니다."
                )
        );
    }

    private String normalize(String content) {
        if (content == null) {
            return "";
        }

        return content.trim();
    }

    private String defaultText(
            String value,
            String defaultValue
    ) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }

    private <T> List<T> safeList(List<T> values) {
        if (values == null) {
            return List.of();
        }

        return values;
    }

    private CoverLetterReviewResultResponse readResult(String resultJson) {
        try {
            return objectMapper.readValue(
                    resultJson,
                    CoverLetterReviewResultResponse.class
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "자기소개서 첨삭 결과 JSON 파싱에 실패했습니다.",
                    e
            );
        }
    }
}