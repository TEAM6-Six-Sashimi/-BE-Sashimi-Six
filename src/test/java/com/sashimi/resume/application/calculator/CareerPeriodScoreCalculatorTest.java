package com.sashimi.resume.application.calculator;

import com.sashimi.resume.domain.model.EmploymentType;
import com.sashimi.resume.domain.model.ResumeCareer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 경력_기간 점수 테스트
class CareerPeriodScoreCalculatorTest {

    private final CareerPeriodScoreCalculator calculator =
            new CareerPeriodScoreCalculator();

    @Test
    void returnsZeroWhenCareerListIsEmpty() {
        int result = calculator.calculate(
                List.of(),
                YearMonth.of(2026, 6)
        );

        assertThat(result).isZero();
    }

    @ParameterizedTest
    @CsvSource({
            "2020-12, 10",
            "2021-01, 25",
            "2022-12, 25",
            "2023-01, 35",
            "2024-12, 35",
            "2025-01, 40"
    })
    void calculatesScoreAtPeriodBoundaries(
            String endYearMonthText,
            int expectedScore
    ) {
        ResumeCareer career = completedCareer(
                YearMonth.of(2020, 1),
                YearMonth.parse(endYearMonthText)
        );

        int result = calculator.calculate(
                List.of(career),
                YearMonth.of(2026, 6)
        );

        assertThat(result).isEqualTo(expectedScore);
    }

    @Test
    void usesCurrentYearMonthForCurrentCareer() {
        ResumeCareer currentCareer =
                new ResumeCareer(
                        "테스트 회사",
                        YearMonth.of(2020, 1),
                        null,
                        true,
                        EmploymentType.FULL_TIME,
                        null,
                        "백엔드 개발자"
                );

        int result = calculator.calculate(
                List.of(currentCareer),
                YearMonth.of(2025, 1)
        );

        assertThat(result).isEqualTo(40);
    }

    @Test
    void sumsMultipleCareerPeriods() {
        ResumeCareer firstCareer =
                completedCareer(
                        YearMonth.of(2020, 1),
                        YearMonth.of(2021, 1)
                );

        ResumeCareer secondCareer =
                completedCareer(
                        YearMonth.of(2021, 2),
                        YearMonth.of(2023, 2)
                );

        int result = calculator.calculate(
                List.of(
                        firstCareer,
                        secondCareer
                ),
                YearMonth.of(2026, 6)
        );

        assertThat(result).isEqualTo(35);
    }

    private ResumeCareer completedCareer(
            YearMonth startYearMonth,
            YearMonth endYearMonth
    ) {
        return new ResumeCareer(
                "테스트 회사",
                startYearMonth,
                endYearMonth,
                false,
                EmploymentType.FULL_TIME,
                null,
                "백엔드 개발자"
        );
    }
}