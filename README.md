# 🍣 -BE-Sashimi-Six

> 핏격(Fit-Gyeok) 백엔드 레포지토리입니다.
> 프로젝트 전체 소개는 [팀 페이지](https://github.com/TEAM6-Six-Sashimi)를 참고해주세요.

---

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| **Language** | Java |
| **Framework** | Spring Boot |
| **Database** | MySQL, Redis |
| **ORM** | JPA |
| **인증** | JWT + Redis Blacklist |
| **AI** | Google Gemini API |
| **OCR** | NAVER Cloud CLOVA OCR |
| **Email** | Mailgun |
| **DB 형상관리** | Flyway |
| **API 문서** | Swagger (OpenAPI) |

---

## 🏗️ 패키지 구조

```
com.sashimi.{domain}
├── presentation/     # Controller, Request/Response DTO
├── application/      # Service, UseCase, Command, Port, Policy
├── domain/           # Domain Model, Repository Interface
└── infrastructure/   # Persistence, AI, Email/External
```

---

## 🔥 개발 워크플로우

```
Issue 생성 → 브랜치 생성 → 개발 → PR 요청 (Slack 알림) → 코드 리뷰 → 승인 → Merge
```

---

## 📌 커밋 컨벤션

```
feat:     기능 추가
fix:      버그 수정
refactor: 구조 개선
style:    UI 변경
docs:     문서 수정
test:     테스트 추가/수정
chore:    기타 작업
```

---

## ✅ 테스트

백엔드 총 **43개 테스트 100% 통과** (Gradle Test)

| 테스트 | 내용 |
|--------|------|
| ArchUnit 계층 의존성 검증 | 클린 아키텍처 규칙 자동 검증 |
| Port 계약 테스트 | InMemory & JPA+H2 구현체 동일 계약 통과 |
| CertificateCommandServiceTest | OCR API 성공/실패 케이스 검증 |
| PaymentCommandServiceTest | 크레딧 차감 → 수강 등록 트랜잭션 검증 |
| EmailVerificationServiceTest | 인증 코드 보안 예외 흐름 검증 |
| UserAccountServiceTest | 탈퇴 시 상태 변경 + 토큰 삭제 흐름 검증 |
