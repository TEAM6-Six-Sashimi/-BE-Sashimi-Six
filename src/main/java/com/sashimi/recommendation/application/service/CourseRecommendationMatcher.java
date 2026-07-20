package com.sashimi.recommendation.application.service;

import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.CourseSearchCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CourseRecommendationMatcher {

    private static final Logger log =
            LoggerFactory.getLogger(CourseRecommendationMatcher.class);

    private static final int MAX_RECOMMENDATION_COUNT = 4;
    private static final int MAX_SEARCH_KEYWORD_COUNT = 10;
    private static final int FALLBACK_COURSE_QUERY_LIMIT = 10;

    private static final int CERTIFICATE_NAME_TITLE_SCORE = 50;
    private static final int CERTIFICATE_NAME_DESCRIPTION_SCORE = 35;
    private static final int CERTIFICATE_SKILL_TITLE_SCORE = 30;
    private static final int CERTIFICATE_SKILL_DESCRIPTION_SCORE = 20;

    private static final int CRITERION_KEYWORD_TITLE_SCORE = 40;
    private static final int CRITERION_KEYWORD_DESCRIPTION_SCORE = 25;
    private static final int CRITERION_SKILL_TITLE_SCORE = 20;
    private static final int CRITERION_SKILL_DESCRIPTION_SCORE = 10;

    private final CourseRepository courseRepository;

    public CourseRecommendationMatcher(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseRecommendation> match(
            List<CertificateRecommendation> certificates,
            List<CourseSearchCriterion> courseSearchCriteria
    ) {
        List<String> searchKeywords = extractSearchKeywords(
                certificates,
                courseSearchCriteria
        );

        List<CourseRecommendation> matchedRecommendations =
                matchByKeywords(
                        certificates,
                        courseSearchCriteria,
                        searchKeywords
                );

        if (matchedRecommendations.size() >= MAX_RECOMMENDATION_COUNT) {
            return matchedRecommendations;
        }

        return fillWithFallbackCourses(matchedRecommendations);
    }

    private List<CourseRecommendation> matchByKeywords(
            List<CertificateRecommendation> certificates,
            List<CourseSearchCriterion> courseSearchCriteria,
            List<String> searchKeywords
    ) {
        if (searchKeywords.isEmpty()) {
            log.debug("강의 추천 키워드 매칭 스킵: 검색 키워드 없음");
            return List.of();
        }

        List<Course> candidateCourses = findCandidateCourses(searchKeywords);

        if (candidateCourses.isEmpty()) {
            log.debug(
                    "강의 추천 키워드 후보 없음: keywordCount={}, certificateCount={}, criteriaCount={}",
                    searchKeywords.size(),
                    sizeOf(certificates),
                    sizeOf(courseSearchCriteria)
            );
            return List.of();
        }

        List<CourseMatchCandidate> candidates = new ArrayList<>();

        collectCertificateCandidates(
                certificates,
                candidateCourses,
                candidates
        );

        collectCourseSearchCriteriaCandidates(
                courseSearchCriteria,
                candidateCourses,
                candidates
        );

        if (candidates.isEmpty()) {
            log.debug(
                    "강의 추천 점수화 결과 없음: candidateCourseCount={}, keywordCount={}",
                    candidateCourses.size(),
                    searchKeywords.size()
            );
            return List.of();
        }

        Map<Long, CourseMatchCandidate> bestCandidateByCourseId =
                selectBestCandidateByCourseId(candidates);

        List<CourseRecommendation> recommendations =
                bestCandidateByCourseId.values()
                        .stream()
                        .sorted(
                                Comparator
                                        .comparingInt(CourseMatchCandidate::score)
                                        .reversed()
                                        .thenComparing(candidate ->
                                                candidate.course().getId()
                                        )
                        )
                        .limit(MAX_RECOMMENDATION_COUNT)
                        .map(this::toRecommendation)
                        .toList();

        log.debug(
                "강의 추천 키워드 매칭 완료: keywordCount={}, candidateCourseCount={}, matchedCourseCount={}",
                searchKeywords.size(),
                candidateCourses.size(),
                recommendations.size()
        );

        return recommendations;
    }

    private List<CourseRecommendation> fillWithFallbackCourses(
            List<CourseRecommendation> matchedRecommendations
    ) {
        Map<Long, CourseRecommendation> recommendationByCourseId =
                new LinkedHashMap<>();

        for (CourseRecommendation recommendation : matchedRecommendations) {
            recommendationByCourseId.put(
                    recommendation.courseId(),
                    recommendation
            );
        }

        List<Course> fallbackCourses =
                courseRepository.findPopularApprovedCourses(
                        FALLBACK_COURSE_QUERY_LIMIT
                );

        for (Course course : fallbackCourses) {
            if (recommendationByCourseId.size() >= MAX_RECOMMENDATION_COUNT) {
                break;
            }

            if (recommendationByCourseId.containsKey(course.getId())) {
                continue;
            }

            recommendationByCourseId.put(
                    course.getId(),
                    new CourseRecommendation(
                            course.getId(),
                            course.getTitle(),
                            null,
                            "인기 강의",
                            "추천 조건과 정확히 일치하는 강의가 부족하여 인기 강의를 추천합니다."
                    )
            );
        }

        List<CourseRecommendation> recommendations =
                recommendationByCourseId.values()
                        .stream()
                        .limit(MAX_RECOMMENDATION_COUNT)
                        .toList();

        log.debug(
                "강의 fallback 추천 완료: originalCount={}, finalCount={}",
                matchedRecommendations.size(),
                recommendations.size()
        );

        return recommendations;
    }

    private List<String> extractSearchKeywords(
            List<CertificateRecommendation> certificates,
            List<CourseSearchCriterion> courseSearchCriteria
    ) {
        Set<String> keywords = new LinkedHashSet<>();

        for (CertificateRecommendation certificate : nullToEmpty(certificates)) {
            addKeyword(keywords, certificate.name());

            for (String skill : nullToEmpty(certificate.relatedSkills())) {
                addKeyword(keywords, skill);
            }
        }

        for (CourseSearchCriterion criterion : nullToEmpty(courseSearchCriteria)) {
            if (!isSupportedCriterion(criterion)) {
                log.debug(
                        "지원하지 않는 강의 검색 기준 제외: recommendationType={}",
                        criterion == null ? null : criterion.recommendationType()
                );
                continue;
            }

            addKeyword(keywords, criterion.keyword());

            for (String skill : nullToEmpty(criterion.relatedSkills())) {
                addKeyword(keywords, skill);
            }
        }

        return keywords.stream()
                .limit(MAX_SEARCH_KEYWORD_COUNT)
                .toList();
    }

    private void addKeyword(
            Set<String> keywords,
            String keyword
    ) {
        String normalizedKeyword = normalize(keyword);

        if (normalizedKeyword.length() < 2) {
            return;
        }

        keywords.add(normalizedKeyword);
    }

    private List<Course> findCandidateCourses(List
