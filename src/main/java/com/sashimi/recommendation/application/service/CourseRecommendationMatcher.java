package com.sashimi.recommendation.application.service;

import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.CourseSearchCriterion;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CourseRecommendationMatcher {

    private static final int MAX_RECOMMENDATION_COUNT = 4;

    private final CourseRepository courseRepository;

    public CourseRecommendationMatcher(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseRecommendation> match(
            List<CertificateRecommendation> certificates,
            List<CourseSearchCriterion> courseSearchCriteria
    ) {
        List<Course> approvedCourses =
                courseRepository.findByStatus(CourseStatus.APPROVED);

        if (approvedCourses.isEmpty()) {
            return List.of();
        }

        Map<Long, CourseRecommendation> matchedCourses =
                new LinkedHashMap<>();

        matchByCertificates(
                certificates,
                approvedCourses,
                matchedCourses
        );

        if (matchedCourses.size() >= MAX_RECOMMENDATION_COUNT) {
            return matchedCourses.values().stream().toList();
        }

        matchByCourseSearchCriteria(
                courseSearchCriteria,
                approvedCourses,
                matchedCourses
        );

        return matchedCourses.values().stream().toList();
    }

    private void matchByCertificates(
            List<CertificateRecommendation> certificates,
            List<Course> approvedCourses,
            Map<Long, CourseRecommendation> matchedCourses
    ) {
        if (certificates == null || certificates.isEmpty()) {
            return;
        }

        for (CertificateRecommendation certificate : certificates) {
            for (Course course : approvedCourses) {
                if (matchedCourses.size() >= MAX_RECOMMENDATION_COUNT) {
                    return;
                }

                MatchResult matchResult = matchCourseByCertificate(
                        certificate,
                        course
                );

                if (!matchResult.matched()) {
                    continue;
                }

                matchedCourses.putIfAbsent(
                        course.getId(),
                        new CourseRecommendation(
                                course.getId(),
                                course.getTitle(),
                                null,
                                matchResult.matchedKeyword(),
                                matchResult.reason()
                        )
                );
            }
        }
    }

    private void matchByCourseSearchCriteria(
            List<CourseSearchCriterion> courseSearchCriteria,
            List<Course> approvedCourses,
            Map<Long, CourseRecommendation> matchedCourses
    ) {
        if (courseSearchCriteria == null || courseSearchCriteria.isEmpty()) {
            return;
        }

        for (CourseSearchCriterion criterion : courseSearchCriteria) {
            for (Course course : approvedCourses) {
                if (matchedCourses.size() >= MAX_RECOMMENDATION_COUNT) {
                    return;
                }

                MatchResult matchResult = matchCourseByCriterion(
                        criterion,
                        course
                );

                if (!matchResult.matched()) {
                    continue;
                }

                matchedCourses.putIfAbsent(
                        course.getId(),
                        new CourseRecommendation(
                                course.getId(),
                                course.getTitle(),
                                null,
                                matchResult.matchedKeyword(),
                                matchResult.reason()
                        )
                );
            }
        }
    }

    private MatchResult matchCourseByCertificate(
            CertificateRecommendation certificate,
            Course course
    ) {
        String courseText = normalize(
                course.getTitle() + " " + safe(course.getDescription())
        );

        String certificateName = safe(certificate.name());

        if (!certificateName.isBlank()
                && courseText.contains(normalize(certificateName))) {
            return new MatchResult(
                    true,
                    certificate.name(),
                    "추천 자격증명과 관련된 강의입니다."
            );
        }

        for (String skill : certificate.relatedSkills()) {
            if (skill == null || skill.isBlank()) {
                continue;
            }

            if (courseText.contains(normalize(skill))) {
                return new MatchResult(
                        true,
                        skill,
                        "추천 자격증의 관련 역량을 보완할 수 있는 강의입니다."
                );
            }
        }

        return new MatchResult(false, null, null);
    }

    private MatchResult matchCourseByCriterion(
            CourseSearchCriterion criterion,
            Course course
    ) {
        String courseText = normalize(
                course.getTitle() + " " + safe(course.getDescription())
        );

        String keyword = safe(criterion.keyword());

        if (!keyword.isBlank()
                && courseText.contains(normalize(keyword))) {
            return new MatchResult(
                    true,
                    keyword,
                    criterion.reason()
            );
        }

        for (String skill : criterion.relatedSkills()) {
            if (skill == null || skill.isBlank()) {
                continue;
            }

            if (courseText.contains(normalize(skill))) {
                return new MatchResult(
                        true,
                        skill,
                        criterion.reason()
                );
            }
        }

        return new MatchResult(false, null, null);
    }

    private String normalize(String value) {
        return safe(value)
                .replaceAll("\\s+", "")
                .toLowerCase();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private record MatchResult(
            boolean matched,
            String matchedKeyword,
            String reason
    ) {
    }
}