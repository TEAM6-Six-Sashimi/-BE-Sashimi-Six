package com.sashimi.course.domain.model;

/** 강의 반려 사유 카테고리 (관리자가 반려 시 선택) */
public enum RejectReasonCategory {
    CURRICULUM_INSUFFICIENT("커리큘럼 구성 미흡"),
    DUPLICATE_SIMILAR("중복/유사 강의"),
    COPYRIGHT("저작권 문제"),
    INAPPROPRIATE_CONTENT("부적절한 내용"),
    INSTRUCTOR_EXPERTISE("강사 전문성 확인 필요");

    private final String label;

    RejectReasonCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
