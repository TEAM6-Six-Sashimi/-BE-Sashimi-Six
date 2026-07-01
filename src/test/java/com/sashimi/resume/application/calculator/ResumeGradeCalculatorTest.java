package com.sashimi.resume.application.calculator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 이력서 등급 기준 테스트
class ResumeGradeCalculatorTest {

    private final ResumeGradeCalculator calculator =
            new ResumeGradeCalculator();

    @ParameterizedTest
    @CsvSource({
            "0, 보완 필요",
            "60, 보완 필요",
            "61, 보통",
            "79, 보통",
            "80, 양호",
            "89, 양호",
            "90, 우수",
            "100, 우수"
    })
    void calculatesGradeByScore(
            int score,
            String expectedGrade
    ) {
        String result = calculator.calculate(score);

        assertThat(result).isEqualTo(expectedGrade);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void rejectsScoreOutsideValidRange(
            int invalidScore
    ) {
        assertThatThrownBy(() ->
                calculator.calculate(invalidScore)
        )
                .isInstanceOf(
                        com.sashimi.global.exception.BusinessException.class
                );
    }
}