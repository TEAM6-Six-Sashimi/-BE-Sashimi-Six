package com.sashimi.resume.application.calculator;

import com.sashimi.resume.domain.model.EducationDegree;
import com.sashimi.resume.domain.model.GraduationStatus;
import com.sashimi.resume.domain.model.ResumeEducation;
import org.springframework.stereotype.Component;

import java.util.List;

/** 학위 점수 계산기 */
@Component
public class EducationScoreCalculator {

    public int calculate(
            List<ResumeEducation> educations
    ) {
        if (educations == null || educations.isEmpty()) {
            return 0;
        }

        return educations.stream()
                .filter(this::isScorable)
                .map(ResumeEducation::degree)
                .mapToInt(this::scoreOf)
                .max()
                .orElse(0);
    }

    private boolean isScorable(
            ResumeEducation education
    ) {
        return education.graduationStatus()
                != GraduationStatus.DROPPED_OUT;
    }

    private int scoreOf(
            EducationDegree degree
    ) {
        return switch (degree) {
            case HIGH_SCHOOL -> 70;
            case ASSOCIATE -> 80;
            case BACHELOR -> 85;
            case MASTER -> 90;
            case DOCTOR -> 100;
        };
    }
}