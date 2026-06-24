package com.sashimi.resume.application.calculator;

import com.sashimi.resume.domain.model.EducationDegree;
import com.sashimi.resume.domain.model.GraduationStatus;
import com.sashimi.resume.domain.model.ResumeEducation;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 학력 점수 테스트
class EducationScoreCalculatorTest {

    private final EducationScoreCalculator calculator =
            new EducationScoreCalculator();

    @Test
    void selectsHighestEducationScore() {
        List<ResumeEducation> educations = List.of(
                education(
                        EducationDegree.HIGH_SCHOOL,
                        GraduationStatus.GRADUATED
                ),
                education(
                        EducationDegree.BACHELOR,
                        GraduationStatus.GRADUATED
                )
        );

        int result = calculator.calculate(educations);

        assertThat(result).isEqualTo(85);
    }

    @Test
    void excludesDroppedOutEducation() {
        List<ResumeEducation> educations = List.of(
                education(
                        EducationDegree.BACHELOR,
                        GraduationStatus.GRADUATED
                ),
                education(
                        EducationDegree.DOCTOR,
                        GraduationStatus.DROPPED_OUT
                )
        );

        int result = calculator.calculate(educations);

        assertThat(result).isEqualTo(85);
    }

    @Test
    void includesEnrolledEducation() {
        List<ResumeEducation> educations = List.of(
                education(
                        EducationDegree.MASTER,
                        GraduationStatus.ENROLLED
                )
        );

        int result = calculator.calculate(educations);

        assertThat(result).isEqualTo(90);
    }

    @Test
    void returnsZeroWhenAllEducationsAreDroppedOut() {
        List<ResumeEducation> educations = List.of(
                education(
                        EducationDegree.BACHELOR,
                        GraduationStatus.DROPPED_OUT
                )
        );

        int result = calculator.calculate(educations);

        assertThat(result).isZero();
    }

    @Test
    void returnsZeroWhenEducationListIsEmpty() {
        int result = calculator.calculate(List.of());

        assertThat(result).isZero();
    }

    private ResumeEducation education(
            EducationDegree degree,
            GraduationStatus status
    ) {
        return new ResumeEducation(
                "테스트 학교",
                YearMonth.of(2020, 3),
                YearMonth.of(2024, 2),
                status,
                "테스트 전공",
                degree,
                null
        );
    }
}