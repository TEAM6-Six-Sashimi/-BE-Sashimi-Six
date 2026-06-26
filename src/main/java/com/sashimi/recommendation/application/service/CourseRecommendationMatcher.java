package com.sashimi.recommendation.application.service;

import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
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
            List<CertificateRecommendation> certificates
    ) {
        if (certificates == null || certificates.isEmpty()) {
            return List.of();
        }

        List<Course> approvedCourses =
                courseRepository.findByStatus(CourseStatus.APPROVED);

        if (approvedCourses.isEmpty()) {
            return List.of();
        }

        Map<Long, CourseRecommendation> matchedCourses =
                new LinkedHashMap<>();

        for (CertificateRecommendation certificate : certificates) {
            for (Course course : approvedCourses) {
                if (matchedCourses.size() >= MAX_RECOMMENDATION_COUNT) {
                    return matchedCourses.values().stream().toList();
                }

                MatchResult matchResult = matchCourse(certificate, course);

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

        return matchedCourses.values().stream().toList();
    }

    private MatchResult matchCourse(
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