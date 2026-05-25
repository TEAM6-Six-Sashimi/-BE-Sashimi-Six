package com.sashimi.global.exception;





public enum ErrorCode {

    INVALID_INPUT_VALUE(400, "COMMON_001", "입력값이 올바르지 않습니다."),
    INVALID_REQUEST_BODY(400, "COMMON_002", "요청 본문이 올바르지 않습니다."),
    MISSING_REQUEST_PARAMETER(400, "COMMON_003", "필수 요청 파라미터가 누락되었습니다."),
    TYPE_MISMATCH(400, "COMMON_004", "요청 파라미터 타입이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(405, "COMMON_005", "지원하지 않는 HTTP 메서드입니다."),

    RESOURCE_NOT_FOUND(404, "COMMON_404", "요청한 대상을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "COMMON_999", "서버 오류가 발생했습니다."),

    UNAUTHORIZED(401, "AUTH_001", "인증이 필요합니다."),
    FORBIDDEN(403, "AUTH_002", "접근 권한이 없습니다."),
    INVALID_TOKEN(401, "AUTH_003", "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(401, "AUTH_004", "유효하지 않은 refresh token입니다."),
    EXPIRED_REFRESH_TOKEN(401, "AUTH_005", "만료된 refresh token입니다."),
    LOGIN_FAILED(401, "AUTH_006", "아이디 또는 비밀번호가 올바르지 않습니다."),

    DUPLICATE_LOGIN_ID(409, "USER_001", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(409, "USER_002", "이미 사용 중인 이메일입니다."),
    INVALID_CURRENT_PASSWORD(400, "USER_003", "현재 비밀번호가 일치하지 않습니다."),
    SAME_AS_OLD_PASSWORD(400, "USER_004", "새 비밀번호는 현재 비밀번호와 달라야 합니다."),
    INACTIVE_USER(403, "USER_005", "비활성화된 회원입니다."),
    USER_NOT_FOUND(404, "USER_404", "사용자를 찾을 수 없습니다."),

    EMAIL_VERIFICATION_NOT_FOUND(404, "VERIFICATION_001", "이메일 인증 요청을 찾을 수 없습니다."),
    EMAIL_VERIFICATION_RESEND_TOO_SOON(429, "VERIFICATION_002", "이메일 인증 코드는 60초 후 다시 요청할 수 있습니다."),
    EMAIL_VERIFICATION_EXPIRED(400, "VERIFICATION_003", "이메일 인증 코드가 만료되었습니다."),
    INVALID_EMAIL_VERIFICATION_CODE(400, "VERIFICATION_004", "이메일 인증 코드가 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED(400, "VERIFICATION_005", "이메일 인증이 완료되지 않았습니다."),
    EMAIL_SEND_FAILED(500, "VERIFICATION_006", "이메일 발송에 실패했습니다.");


    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
