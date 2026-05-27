package com.sashimi.member.domain.model;

public enum CertificationStatus {
    PENDING,   // OCR 처리 중
    VERIFIED,  // 등록 완료
    REJECTED   // 등록 거부
}