package com.sashimi.resume.application.calculator;

import com.sashimi.resume.domain.model.EmploymentType;
import com.sashimi.resume.domain.model.ResumeCareer;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 경력_갯수 점수 테스트
class CareerCountScoreCalculatorTest {

    private final CareerCountScoreCalculator calculator =
            new CareerCountScoreCalculator();

    @Test
    void returnsZeroWhenCareerListIsEmpty() {
        int result = calculator.calculate(List.of());

        assertThat(result).isZero();
    }

    @Test
    void returnsFifteenForOneCareer() {
        int result = calculator.calculate(
                List.of(career("회사 1"))
        );

        assertThat(result).isEqualTo(15);
    }

    @Test
    void returnsTwentyFiveForTwoCareers() {
        int result = calculator.calculate(
                List.of(
                        career("회사 1"),
                        career("회사 2")
                )
        );

        assertThat(result).isEqualTo(25);
    }

    @Test
    void returnsThirtyForThreeOrMoreCareers() {
        int result = calculator.calculate(
                List.of(
                        career("회사 1"),
                        career("회사 2"),
                        career("회사 3"),
                        career("회사 4")
                )
        );

        assertThat(result).isEqualTo(30);
    }

    private ResumeCareer career(
            String companyName
    ) {
        return new ResumeCareer(
                companyName,
                YearMonth.of(2020, 1),
                YearMonth.of(2021, 1),
                false,
                EmploymentType.FULL_TIME,
                null,
                "백엔드 개발자"
        );
    }
}