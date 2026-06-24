package com.sashimi.resume.application.calculator;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

// 이력서 항목 중 자격증 점수 계산기
@Component
public class CertificateScoreCalculator {

    public int calculate(
            int verifiedCertificateCount
    ) {
        if (verifiedCertificateCount < 0) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (verifiedCertificateCount == 0) {
            return 0;
        }

        if (verifiedCertificateCount == 1) {
            return 70;
        }

        if (verifiedCertificateCount == 2) {
            return 80;
        }

        if (verifiedCertificateCount == 3) {
            return 90;
        }

        return 100;
    }
}