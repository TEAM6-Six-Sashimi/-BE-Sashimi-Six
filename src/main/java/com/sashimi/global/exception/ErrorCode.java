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
    INVALID_REFERRAL_CODE(400, "USER_006", "유효하지 않은 추천인 코드입니다."),
    USER_NOT_FOUND(404, "USER_404", "사용자를 찾을 수 없습니다."),

    EMAIL_VERIFICATION_NOT_FOUND(404, "VERIFICATION_001", "이메일 인증 요청을 찾을 수 없습니다."),
    EMAIL_VERIFICATION_RESEND_TOO_SOON(429, "VERIFICATION_002", "이메일 인증 코드는 60초 후 다시 요청할 수 있습니다."),
    EMAIL_VERIFICATION_EXPIRED(400, "VERIFICATION_003", "이메일 인증 코드가 만료되었습니다."),
    INVALID_EMAIL_VERIFICATION_CODE(400, "VERIFICATION_004", "이메일 인증 코드가 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED(400, "VERIFICATION_005", "이메일 인증이 완료되지 않았습니다."),
    EMAIL_SEND_FAILED(500, "VERIFICATION_006", "이메일 발송에 실패했습니다."),


    COURSE_NOT_FOUND(404, "COURSE_404", "강의를 찾을 수 없습니다."),
    COURSE_NOT_MODIFIABLE(400, "COURSE_001", "수정할 수 없는 상태의 강의입니다."),
    COURSE_NOT_DELETABLE(400, "COURSE_002", "삭제할 수 없는 상태의 강의입니다."),
    COURSE_NOT_PENDING(400, "COURSE_003", "승인 대기 상태가 아닌 강의입니다."),
    COURSE_FORBIDDEN(403, "COURSE_004", "해당 강의에 대한 권한이 없습니다."),
    COURSE_NOT_PURCHASABLE(400, "COURSE_005", "구매할 수 없는 강의입니다."),

    CATEGORY_NOT_FOUND(404, "CATEGORY_404", "카테고리를 찾을 수 없습니다."),
    NCS_INFO_NOT_FOUND(404, "NCS_404", "NCS 정보를 찾을 수 없습니다."),

    CART_ITEM_NOT_FOUND(404, "CART_001", "장바구니 항목을 찾을 수 없습니다."),
    CART_ITEM_ALREADY_EXISTS(409, "CART_002", "이미 장바구니에 담긴 강의입니다."),
    CART_EMPTY_SELECTION(400, "CART_003", "결제할 강의를 선택해주세요."),
    CART_INVALID_SELECTION(400, "CART_004", "장바구니 선택 정보가 올바르지 않습니다."),

    ENROLLMENT_ALREADY_EXISTS(409, "ENROLLMENT_001", "이미 수강 중인 강의입니다."),
    ENROLLMENT_CREATE_FAILED(500, "ENROLLMENT_002", "수강 등록에 실패했습니다."),
    ENROLLMENT_EXPIRED(403, "ENROLLMENT_003", "수강 가능 기간이 만료되었습니다."),

    PAYMENT_ORDER_NOT_FOUND(404, "PAYMENT_001", "주문을 찾을 수 없습니다."),
    PAYMENT_EMPTY_COURSE(400, "PAYMENT_002", "결제할 강의가 없습니다."),
    PAYMENT_NOT_FOUND(404, "PAYMENT_003", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ALREADY_PROCESSED(409, "PAYMENT_004", "이미 처리된 결제입니다."),
    PAYMENT_INVALID_STATUS(400, "PAYMENT_005", "처리할 수 없는 결제 상태입니다."),
    PAYMENT_AMOUNT_MISMATCH(400, "PAYMENT_006", "결제 금액이 일치하지 않습니다."),
    PAYMENT_APPROVAL_FAILED(502, "PAYMENT_007", "외부 결제 승인에 실패했습니다."),
    PAYMENT_INVALID_CHECKOUT_REQUEST(400, "PAYMENT_008", "결제 요청 정보가 올바르지 않습니다."),
    PAYMENT_AGREEMENT_REQUIRED(400, "PAYMENT_009", "결제 진행을 위해 결제 동의가 필요합니다."),
    PAYMENT_COURSE_ID_REQUIRED(400, "PAYMENT_010", "단일 강의 결제에는 강의 ID가 필요합니다."),
    PAYMENT_CART_COURSE_ID_NOT_ALLOWED(400, "PAYMENT_011", "장바구니 결제에는 강의 ID를 전달할 수 없습니다."),

    CREDIT_INVALID_AMOUNT(400, "CREDIT_001", "크레딧 금액이 올바르지 않습니다."),
    CREDIT_INSUFFICIENT_BALANCE(400, "CREDIT_002", "크레딧 잔액이 부족합니다."),
    CREDIT_CHARGE_AMOUNT_TOO_SMALL(400, "CREDIT_003", "최소 충전 금액보다 작습니다."),
    CREDIT_CHARGE_AMOUNT_UNIT_INVALID(400, "CREDIT_004", "충전 금액 단위가 올바르지 않습니다."),
    CREDIT_CHARGE_PAYMENT_NOT_FOUND(404, "CREDIT_005", "크레딧 충전 결제 요청을 찾을 수 없습니다."),
    CREDIT_CHARGE_PAYMENT_ALREADY_PROCESSED(409, "CREDIT_006", "이미 처리된 크레딧 충전 결제입니다."),
    CREDIT_CHARGE_PAYMENT_AMOUNT_MISMATCH(400, "CREDIT_007", "크레딧 충전 결제 금액이 일치하지 않습니다."),
    CREDIT_CHARGE_PAYMENT_FORBIDDEN(403, "CREDIT_008", "해당 크레딧 충전 결제에 접근할 수 없습니다."),
    CREDIT_EXTERNAL_PAYMENT_FAILED(502, "CREDIT_009", "외부 결제 승인에 실패했습니다."),

    SUBSCRIPTION_NOT_FOUND(404, "SUBSCRIPTION_001", "구독권을 찾을 수 없습니다."),
    SUBSCRIPTION_ALREADY_ACTIVE(409, "SUBSCRIPTION_002", "이미 활성화된 구독권이 있습니다."),
    SUBSCRIPTION_INVALID_PLAN(400, "SUBSCRIPTION_003", "올바르지 않은 구독권 상품입니다."),
    SUBSCRIPTION_RENEWAL_FAILED(400, "SUBSCRIPTION_004", "구독권 자동 갱신에 실패했습니다."),

    INVALID_INPUT(400, "MEMBER_001", "입력값이 올바르지 않습니다."),
    ALREADY_APPLIED(400, "MEMBER_002","이미 강사 신청이 진행 중입니다."),
    APPLICATION_NOT_FOUND(404,"MEMBER_003", "신청을 찾을 수 없습니다."),
    INVALID_APPLICATION_STATUS(400, "MEMBER_004", "처리할 수 없는 신청 상태입니다."),
    CERTIFICATE_OCR_FAILED(400, "MEMBER_005", "자격증 OCR 검증에 실패했습니다."),
    CERTIFICATE_NOT_FOUND(404, "MEMBER_006", "자격증을 찾을 수 없습니다."),
    RESUME_PARSE_FAILED(400, "MEMBER_007", "이력서 파싱 처리에 실패했습니다. 주요 이력 항목을 확인해주세요."),
    RESUME_INVALID_FORMAT(400, "MEMBER_008", "이력서 파일은 .docx 형식만 업로드 가능합니다."),

    REVIEW_ALREADY_EXISTS(409, "REVIEW_001", "이미 해당 강의에 리뷰를 작성하셨습니다."),
    REVIEW_NOT_ENROLLED(403, "REVIEW_002", "수강 중인 강의에만 리뷰를 작성할 수 있습니다."),
    REVIEW_PROGRESS_REQUIRED(400, "REVIEW_003", "수강 후 리뷰를 작성할 수 있습니다."),
    REVIEW_NOT_FOUND(404, "REVIEW_404", "수강평을 찾을 수 없습니다."),
    REVIEW_FORBIDDEN(403, "REVIEW_004", "본인이 작성한 수강평만 삭제할 수 있습니다."),
    REVIEW_ALREADY_REPORTED(409, "REVIEW_005", "이미 신고한 리뷰입니다."),
    REVIEW_SELF_REPORT_FORBIDDEN(403, "REVIEW_006", "본인이 작성한 수강평은 신고할 수 없습니다."),

    AI_API_KEY_MISSING(500, "AI_001", "AI API Key가 설정되지 않았습니다."),
    AI_API_KEY_NOT_RESOLVED(500, "AI_002", "AI API Key 환경변수가 정상적으로 치환되지 않았습니다."),
    AI_API_CALL_FAILED(502, "AI_003", "AI API 호출에 실패했습니다."),
    AI_RESPONSE_EMPTY(502, "AI_004", "AI 응답이 비어 있습니다."),
    AI_RESPONSE_PARSE_FAILED(502, "AI_005", "AI 응답 결과를 파싱할 수 없습니다."),
    AI_PROMPT_NOT_FOUND(404, "AI_006", "활성화된 AI 프롬프트를 찾을 수 없습니다."),

    RESUME_NOT_FOUND(404, "RESUME_404", "이력서를 찾을 수 없습니다."),

    JOB_POSTING_RECOMMENDATION_NOT_FOUND(404, "RECOMMENDATION_404", "채용공고 추천 결과를 찾을 수 없습니다."),

    FILE_EMPTY(400, "FILE_001", "업로드할 파일이 없습니다."),
    FILE_INVALID_TYPE(400, "FILE_002", "지원하지 않는 파일 형식입니다. (jpg, png, gif, webp만 허용)"),
    FILE_UPLOAD_FAILED(500, "FILE_003", "파일 업로드에 실패했습니다.");


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
