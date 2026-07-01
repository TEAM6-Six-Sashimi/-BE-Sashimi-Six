package com.sashimi.resume.application.calculator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 자격증 점수 테스트
class CertificateScoreCalculatorTest {

    private final CertificateScoreCalculator calculator =
            new CertificateScoreCalculator();

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "1, 70",
            "2, 80",
            "3, 90",
            "4, 100",
            "10, 100"
    })
    void calculatesScoreByCertificateCount(
            int certificateCount,
            int expectedScore
    ) {
        int result = calculator.calculate(
                certificateCount
        );

        assertThat(result).isEqualTo(expectedScore);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10})
    void rejectsNegativeCertificateCount(
            int invalidCount
    ) {
        assertThatThrownBy(() ->
                calculator.calculate(invalidCount)
        )
                .isInstanceOf(
                        com.sashimi.global.exception.BusinessException.class
                );
    }
}