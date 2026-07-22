package com.sashimi.coverletter.application.service;

import com.sashimi.coverletter.domain.model.CoverLetterQuestion;
import com.sashimi.coverletter.infrastructure.persistence.CoverLetterJpaEntity;
import com.sashimi.coverletter.infrastructure.persistence.SpringDataCoverLetterRepository;
import com.sashimi.coverletter.presentation.api.request.CoverLetterItemRequest;
import com.sashimi.coverletter.presentation.api.request.UpdateCoverLettersRequest;
import com.sashimi.coverletter.presentation.api.response.CoverLetterItemResponse;
import com.sashimi.coverletter.presentation.api.response.CoverLettersResponse;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CoverLetterService {

    private final SpringDataCoverLetterRepository repository;

    public CoverLetterService(
            SpringDataCoverLetterRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public CoverLettersResponse getMyCoverLetters(Long userId) {
        Map<CoverLetterQuestion, String> contentMap =
                getContentMap(userId);

        return toResponse(contentMap);
    }

    @Transactional
    public CoverLettersResponse updateMyCoverLetters(
            Long userId,
            UpdateCoverLettersRequest request
    ) {
        if (request == null || request.items() == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST_BODY
            );
        }

        validateDuplicatedQuestionKeys(request.items());

        for (CoverLetterItemRequest item : request.items()) {
            if (item == null) {
                throw new BusinessException(
                        ErrorCode.INVALID_INPUT_VALUE
                );
            }

            CoverLetterQuestion question =
                    CoverLetterQuestion.from(item.questionKey());

            String content = normalizeContent(item.content());

            validateContentLength(
                    question,
                    content
            );

            CoverLetterJpaEntity entity = repository
                    .findByUserIdAndQuestionKey(
                            userId,
                            question
                    )
                    .orElseGet(() -> CoverLetterJpaEntity.create(
                            userId,
                            question,
                            ""
                    ));

            entity.updateContent(content);
            repository.save(entity);
        }

        return getMyCoverLetters(userId);
    }

    private Map<CoverLetterQuestion, String> getContentMap(Long userId) {
        Map<CoverLetterQuestion, String> contentMap =
                new EnumMap<>(CoverLetterQuestion.class);

        repository.findAllByUserId(userId)
                .forEach(entity -> contentMap.put(
                        entity.getQuestionKey(),
                        entity.getContent()
                ));

        return contentMap;
    }

    private CoverLettersResponse toResponse(
            Map<CoverLetterQuestion, String> contentMap
    ) {
        List<CoverLetterItemResponse> items =
                java.util.Arrays.stream(CoverLetterQuestion.values())
                        .sorted(java.util.Comparator.comparingInt(
                                CoverLetterQuestion::displayOrder
                        ))
                        .map(question -> CoverLetterItemResponse.of(
                                question,
                                contentMap.getOrDefault(
                                        question,
                                        ""
                                )
                        ))
                        .toList();

        return new CoverLettersResponse(items);
    }

    private void validateDuplicatedQuestionKeys(
            List<CoverLetterItemRequest> items
    ) {
        Set<String> questionKeys = new HashSet<>();

        for (CoverLetterItemRequest item : items) {
            if (item == null || item.questionKey() == null) {
                throw new BusinessException(
                        ErrorCode.COVER_LETTER_INVALID_QUESTION
                );
            }

            if (!questionKeys.add(item.questionKey())) {
                throw new BusinessException(
                        ErrorCode.COVER_LETTER_INVALID_QUESTION
                );
            }
        }
    }

    private String normalizeContent(String content) {
        if (content == null) {
            return "";
        }

        return content;
    }

    private void validateContentLength(
            CoverLetterQuestion question,
            String content
    ) {
        if (content.length() > question.maxLength()) {
            throw new BusinessException(
                    ErrorCode.COVER_LETTER_CONTENT_TOO_LONG
            );
        }
    }
}