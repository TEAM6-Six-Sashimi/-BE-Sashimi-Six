package com.sashimi.resume.application.calculator;

import com.sashimi.resume.application.result.ResumeScoreResult;
import com.sashimi.resume.domain.model.EducationDegree;
import com.sashimi.resume.domain.model.EmploymentType;
import com.sashimi.resume.domain.model.GraduationStatus;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeEducation;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 이력서 총 점수 및 등급 테스트
class ResumeScoreCalculatorTest {

    private final ResumeScoreCalculator calculator =
            new ResumeScoreCalculator(
                    new EducationScoreCalculator(),
                    new CareerCountScoreCalculator(),
                    new CareerPeriodScoreCalculator(),
                    new CertificateScoreCalculator(),
                    new ResumeGradeCalculator()
            );

    @Test
    void calculatesOverallResumeScore() {
        Resume resume = experiencedResume();

        ResumeScoreResult result = calculator.calculate(
                resume,
                2,
                30,
                YearMonth.of(2026, 6)
        );

        assertThat(
                result.education().score()
        ).isEqualTo(85);

        assertThat(
                result.careerCountScore()
        ).isEqualTo(25);

        assertThat(
                result.careerPeriodScore()
        ).isEqualTo(35);

        assertThat(
                result.careerContinuityScore()
        ).isEqualTo(30);

        assertThat(
                result.career().score()
        ).isEqualTo(90);

        assertThat(
                result.certificate().score()
        ).isEqualTo(80);

        assertThat(
                result.overallScore()
        ).isEqualTo(85);

        assertThat(
                result.overallGrade()
        ).isEqualTo("양호");
    }

    @Test
    void returnsZeroCareerScoreForEntryLevelResume() {
        Resume resume = entryLevelResume();

        ResumeScoreResult result = calculator.calculate(
                resume,
                0,
                0,
                YearMonth.of(2026, 6)
        );

        assertThat(
                result.careerCountScore()
        ).isZero();

        assertThat(
                result.careerPeriodScore()
        ).isZero();

        assertThat(
                result.careerContinuityScore()
        ).isZero();

        assertThat(
                result.career().score()
        ).isZero();

        assertThat(
                result.overallScore()
        ).isEqualTo(28);

        assertThat(
                result.overallGrade()
        ).isEqualTo("보완 필요");
    }

    @Test
    void rejectsInvalidContinuityScore() {
        Resume resume = experiencedResume();

        assertThatThrownBy(() ->
                calculator.calculate(
                        resume,
                        2,
                        15,
                        YearMonth.of(2026, 6)
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }

    @Test
    void rejectsContinuityScoreWhenCareerIsEmpty() {
        Resume resume = entryLevelResume();

        assertThatThrownBy(() ->
                calculator.calculate(
                        resume,
                        0,
                        10,
                        YearMonth.of(2026, 6)
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }

    @Test
    void rejectsZeroContinuityScoreWhenCareerExists() {
        Resume resume = experiencedResume();

        assertThatThrownBy(() ->
                calculator.calculate(
                        resume,
                        2,
                        0,
                        YearMonth.of(2026, 6)
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }

    private Resume experiencedResume() {
        return Resume.create(
                1L,
                List.of(bachelorEducation()),
                false,
                List.of(
                        completedCareer(
                                "회사 1",
                                YearMonth.of(2020, 1),
                                YearMonth.of(2021, 1)
                        ),
                        completedCareer(
                                "회사 2",
                                YearMonth.of(2021, 2),
                                YearMonth.of(2023, 2)
                        )
                ),
                true
        ).withId(1L);
    }

    private Resume entryLevelResume() {
        return Resume.create(
                1L,
                List.of(bachelorEducation()),
                true,
                List.of(),
                true
        ).withId(1L);
    }

    private ResumeEducation bachelorEducation() {
        return new ResumeEducation(
                "한국대학교",
                YearMonth.of(2016, 3),
                YearMonth.of(2020, 2),
                EducationDegree.BACHELOR,
                "컴퓨터공학과",
                GraduationStatus.GRADUATED,
                null
        );
    }

    private ResumeCareer completedCareer(
            String companyName,
            YearMonth startYearMonth,
            YearMonth endYearMonth
    ) {
        return new ResumeCareer(
                companyName,
                startYearMonth,
                endYearMonth,
                false,
                EmploymentType.FULL_TIME,
                null,
                "백엔드 개발자"
        );
    }
}