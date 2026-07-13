package com.sashimi.coverletter.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sashimi.ai.application.policy.AiFeatureAccessPolicy;
import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.domain.model.AiRequestStatus;
import com.sashimi.ai.infrastructure.persistence.AiRequestHistoryJpaEntity;
import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.coverletter.domain.model.CoverLetterQuestion;
import com.sashimi.coverletter.infrastructure.persistence.CoverLetterJpaEntity;
import com.sashimi.coverletter.infrastructure.persistence.SpringDataCoverLetterRepository;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class CoverLetterReviewService {

    private static final int TOTAL_QUESTION_COUNT = 5;

    private final SpringDataCoverLetterRepository coverLetterRepository;
    private final SpringDataAiRequestHistoryRepository aiRequestHistoryRepository;
    private final AiFeatureAccessPolicy aiFeatureAccessPolicy;
    private final ObjectMapper objectMapper;

    public CoverLetterReviewService(
            SpringDataCoverLetterRepository coverLetterRepository,
            SpringDataAiRequestHistoryRepository aiRequestHistoryRepository,
            AiFeatureAccessPolicy aiFeatureAccessPolicy,
            ObjectMapper objectMapper
    ) {
        this.coverLetterRepository = coverLetterRepository;
        this.aiRequestHistoryRepository = aiRequestHistoryRepository;
        this.aiFeatureAccessPolicy = aiFeatureAccessPolicy;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CoverLetterReviewCreateResponse review(Long userId) {
        Map<CoverLetterQuestion, String> contentMap = getContentMap(userId);

        if (isAllBlank(contentMap)) {
            throw new BusinessException(ErrorCode.COVER_LETTER_EMPTY);
        }

        aiFeatureAccessPolicy.validate(
                userId,
                AiMetrics.FEATURE_COVER_LETTER_REVIEW
        );

        String requestSnapshotJson = writeJson(contentMap);

        AiRequestHistoryJpaEntity history = aiRequestHistoryRepository.save(
                AiRequestHistoryJpaEntity.started(
                        userId,
                        AiFeatureType.COVER_LETTER_REVIEW,
                        requestSnapshotJson
                )
        );

        CoverLetterReviewResultResponse result = createStubResult(
                history.getId(),
                history.getCreatedAt(),
                contentMap
        );

        history.complete(writeJson(result));

        return new CoverLetterReviewCreateResponse(
                history.getId(),
                history.getCreatedAt(),
                result.summary()
        );
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
                .findByIdAndUserIdAndFeatureType(
                        reviewId,
                        userId,
                        AiFeatureType.COVER_LETTER_REVIEW
                )
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.COVER_LETTER_REVIEW_NOT_FOUND
                ));

        return readResult(history.getResultJson());
    }

    private Map<CoverLetterQuestion, String> getContentMap(Long userId) {
        Map<CoverLetterQuestion, String> contentMap =
                new EnumMap<>(CoverLetterQuestion.class);

        coverLetterRepository.findAllByUserId(userId)
                .forEach(entity -> contentMap.put(
                        entity.getQuestionKey(),
                        normalize(entity.getContent())
                ));

        return contentMap;
    }

    private boolean isAllBlank(Map<CoverLetterQuestion, String> contentMap) {
        return Arrays.stream(CoverLetterQuestion.values())
                .map(question -> contentMap.getOrDefault(question, ""))
                .allMatch(String::isBlank);
    }

    private CoverLetterReviewResultResponse createStubResult(
            Long reviewId,
            LocalDateTime createdAt,
            Map<CoverLetterQuestion, String> contentMap
    ) {
        List<CoverLetterReviewQuestionResponse> questions =
                Arrays.stream(CoverLetterQuestion.values())
                        .sorted(java.util.Comparator.comparingInt(
                                CoverLetterQuestion::displayOrder
                        ))
                        .map(question -> createStubQuestionResult(
                                question,
                                contentMap.getOrDefault(question, "")
                        ))
                        .toList();

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

        CoverLetterReviewSummaryResponse summary =
                new CoverLetterReviewSummaryResponse(
                        completedCount,
                        TOTAL_QUESTION_COUNT,
                        needRevisionCount,
                        recommendedCount,
                        spellingCorrectionCount,
                        0,
                        calculateAverageSentenceLength(contentMap),
                        "전체적으로 작성 방향은 좋습니다. 다만 일부 문항은 경험, 행동, 결과를 더 구체적으로 작성하면 전달력이 좋아집니다."
                );

        return new CoverLetterReviewResultResponse(
                reviewId,
                createdAt,
                summary,
                questions
        );
    }

    private CoverLetterReviewQuestionResponse createStubQuestionResult(
            CoverLetterQuestion question,
            String content
    ) {
        String normalizedContent = normalize(content);

        if (normalizedContent.isBlank()) {
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
                    "자기소개서를 작성해주세요.",
                    ""
            );
        }

        String status = normalizedContent.length() < 80
                ? "NEEDS_REVISION"
                : "RECOMMENDED";

        List<CoverLetterSpellingCorrectionResponse> corrections =
                createStubCorrections(normalizedContent);

        return new CoverLetterReviewQuestionResponse(
                question.displayOrder(),
                question.name(),
                question.title(),
                question.maxLength(),
                status,
                createSummaryFeedback(status),
                corrections.size(),
                1,
                1,
                normalizedContent,
                corrections,
                "현재 답변은 핵심 내용은 드러나지만, 구체적인 행동 과정과 결과가 조금 더 보완되면 좋습니다.",
                "문제 상황을 설명한 뒤 원인 분석, 본인의 행동, 결과, 배운 점 순서로 정리하면 더 설득력 있는 답변이 됩니다."
        );
    }

    private List<CoverLetterSpellingCorrectionResponse> createStubCorrections(
            String content
    ) {
        if (content.contains("할수")) {
            return List.of(
                    new CoverLetterSpellingCorrectionResponse(
                            "할수",
                            "할 수"
                    )
            );
        }

        return List.of();
    }

    private String createSummaryFeedback(String status) {
        if ("NEEDS_REVISION".equals(status)) {
            return "내용의 구체성과 지원 직무와의 연결성을 보완해주세요.";
        }

        return "작성 방향은 좋으며, 경험과 결과를 조금 더 구체화하면 좋습니다.";
    }

    private int calculateAverageSentenceLength(
            Map<CoverLetterQuestion, String> contentMap
    ) {
        String joinedContent = String.join(
                " ",
                contentMap.values()
        ).trim();

        if (joinedContent.isBlank()) {
            return 0;
        }

        String[] sentences = joinedContent.split("[.!?。！？]");
        int sentenceCount = Math.max(sentences.length, 1);

        return joinedContent.length() / sentenceCount;
    }

    private String normalize(String content) {
        if (content == null) {
            return "";
        }

        return content.trim();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("자기소개서 첨삭 결과 JSON 변환에 실패했습니다.", e);
        }
    }

    private CoverLetterReviewResultResponse readResult(String resultJson) {
        try {
            return objectMapper.readValue(
                    resultJson,
                    CoverLetterReviewResultResponse.class
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("자기소개서 첨삭 결과 JSON 파싱에 실패했습니다.", e);
        }
    }
}