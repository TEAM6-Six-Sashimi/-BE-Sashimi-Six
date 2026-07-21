package com.sashimi.coverletter.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sashimi.ai.application.policy.AiFeatureAccessPolicy;
import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.infrastructure.fastapi.coverletter.FastApiCoverLetterReviewRequest;
import com.sashimi.ai.infrastructure.persistence.AiRequestHistoryJpaEntity;
import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.coverletter.domain.model.CoverLetterQuestion;
import com.sashimi.coverletter.infrastructure.persistence.SpringDataCoverLetterRepository;
import com.sashimi.coverletter.presentation.api.response.CoverLetterReviewResultResponse;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class CoverLetterReviewTransactionService {

    private final SpringDataCoverLetterRepository coverLetterRepository;
    private final SpringDataAiRequestHistoryRepository aiRequestHistoryRepository;
    private final AiFeatureAccessPolicy aiFeatureAccessPolicy;
    private final ObjectMapper objectMapper;

    public CoverLetterReviewTransactionService(
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
    public CoverLetterReviewPreparation prepareReview(Long userId) {
        Map<CoverLetterQuestion, String> contentMap = getContentMap(userId);

        if (isAllBlank(contentMap)) {
            throw new BusinessException(ErrorCode.COVER_LETTER_EMPTY);
        }

        aiFeatureAccessPolicy.validate(
                userId,
                AiMetrics.FEATURE_COVER_LETTER_REVIEW
        );

        FastApiCoverLetterReviewRequest request =
                createFastApiRequest(contentMap);

        AiRequestHistoryJpaEntity history = aiRequestHistoryRepository.save(
                AiRequestHistoryJpaEntity.started(
                        userId,
                        AiFeatureType.COVER_LETTER_REVIEW,
                        writeJson(request)
                )
        );

        return new CoverLetterReviewPreparation(
                history.getId(),
                history.getCreatedAt(),
                Map.copyOf(contentMap),
                request
        );
    }

    @Transactional
    public void completeReview(
            Long historyId,
            CoverLetterReviewResultResponse result
    ) {
        AiRequestHistoryJpaEntity history =
                aiRequestHistoryRepository.findById(historyId)
                        .orElseThrow(() -> new IllegalStateException(
                                "AI 요청 이력을 찾을 수 없습니다."
                        ));

        history.complete(writeJson(result));
    }

    @Transactional
    public void failReview(
            Long historyId,
            RuntimeException exception
    ) {
        aiRequestHistoryRepository.findById(historyId)
                .ifPresent(history ->
                        history.fail(exception.getClass().getSimpleName())
                );
    }

    private FastApiCoverLetterReviewRequest createFastApiRequest(
            Map<CoverLetterQuestion, String> contentMap
    ) {
        List<FastApiCoverLetterReviewRequest.Question> questions =
                Arrays.stream(CoverLetterQuestion.values())
                        .sorted(Comparator.comparingInt(
                                CoverLetterQuestion::displayOrder
                        ))
                        .map(question -> toFastApiQuestion(
                                question,
                                contentMap.getOrDefault(question, "")
                        ))
                        .filter(question -> !question.content().isBlank())
                        .toList();

        return new FastApiCoverLetterReviewRequest(
                questions,
                null,
                null
        );
    }

    private FastApiCoverLetterReviewRequest.Question toFastApiQuestion(
            CoverLetterQuestion question,
            String content
    ) {
        return new FastApiCoverLetterReviewRequest.Question(
                question.name(),
                question.title(),
                normalize(content)
        );
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
            throw new IllegalStateException(
                    "자기소개서 첨삭 결과 JSON 변환에 실패했습니다.",
                    e
            );
        }
    }
}