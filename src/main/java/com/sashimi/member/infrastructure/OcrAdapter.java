package com.sashimi.member.infrastructure;

import com.sashimi.member.application.port.OcrPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class OcrAdapter implements OcrPort {

    @Override
    public OcrResult extractCertificateInfo(String fileUrl) {
        // TODO: 실제 OCR API 연동 (Naver Clova OCR 등)
        // 지금은 임시로 성공 처리
        return new OcrResult(
                "정보처리기사",
                "한국산업인력공단",
                LocalDate.of(2024, 1, 15),
                true
        );
    }
}