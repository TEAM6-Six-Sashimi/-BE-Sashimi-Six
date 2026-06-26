package com.sashimi.course.application.query;

/**
 * 강의 상세 조회 시 호출자의 관점.
 * - PUBLIC   : 비로그인 또는 미수강 (미리보기 영상만)
 * - ENROLLED : 수강 중인 학생 (전체 영상·자료 + 진행률)
 * - OWNER    : 본인 강의를 보는 강사 (모든 상태 + 관리 정보)
 * - ADMIN    : 관리자 (모든 상태 + 관리 정보)
 */
public enum CourseViewerType {
    PUBLIC, ENROLLED, OWNER, ADMIN
}
