# 🎯 핏격 (Fit-Gyeok)

> **당신에게 딱 맞는 자격증을 합격하기 위한 강의가 여기에,**
>
> 채용공고 분석부터 학습, 취업 지원까지 연결되는 AI 기반 맞춤형 자격증 LMS 플랫폼

---

## 📌 프로젝트 소개

취업 준비생의 자격증 취득을 단순 강의 제공을 넘어, **채용공고 기반 역량 분석 → 맞춤형 강의 추천 → AI 이력서 평가 → 취업 지원**까지 하나의 흐름으로 연결하는 통합 학습 플랫폼입니다.

- **분야**: 국가기술자격증 교육 / 취업 연계형 LMS / AI 기반 맞춤 학습
- **주요 대상**: 자격증 취득 준비 학습자, 취업·이직 준비 구직자, 직무 역량 향상을 목표로 하는 사용자

---

## 👥 팀 소개 — SIX SASHIMI

| 이름 | 파트 | 역할 |
|------|------|------|
| 김가영 | Frontend | FE-PM |
| 김채린 | Backend | DBA |
| 박민서 | Backend | PO/PL |
| 박정민 | Backend | DBA |
| 윤영지 | Frontend | SCM |
| 정유지 | Backend | SCM |
| 지석범 | Backend | BE-PM |

---

## ✨ 핵심 기능

### 🤖 AI 기반 채용공고 맞춤 추천
채용공고 URL 또는 텍스트를 입력하면 Gemini AI가 요구 기술·역량을 분석하고, 사용자의 보유 역량과 비교해 부족한 부분을 시각화하여 추천 강의와 학습 로드맵을 제공합니다.

### 📄 AI 이력서 작성 및 평가
기본 정보, 학력, 경력, 기술 스택, 자격증을 입력하면 Gemini AI가 항목별로 평가하고 완성도 점수와 개선 필요 항목을 제공합니다.

### 🪪 자격증 OCR 자동 인증
자격증 파일을 업로드하면 NAVER Cloud CLOVA OCR이 자격증명, 발급기관, 발급일을 자동 추출해 검증합니다. 한국어 인식에 최적화된 OCR을 적용했습니다.

### 🛒 강의 수강 및 결제
장바구니 → 크레딧 결제 → 수강 신청 등록까지 하나의 트랜잭션으로 처리되며, 1개라도 실패 시 전체 롤백됩니다.

### 📧 이메일 인증
회원가입, 비밀번호 재설정, 이메일 변경 시 Mailgun 기반 인증 코드를 발송합니다. 인증 코드는 10분 만료, 재발송 60초 제한이 적용됩니다.

### 🔐 인증 / 인가
JWT 기반 인증에 Redis Blacklist를 적용해 로그아웃 후 탈취된 토큰을 즉시 무효화합니다. 권한은 STUDENT / INSTRUCTOR / ADMIN으로 분리됩니다.

---

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| **Frontend** | Next.js, TypeScript, Tailwind CSS, shadcn/ui |
| **Backend** | Spring Boot, Java |
| **Database** | MySQL, Redis |
| **AI** | Google Gemini API |
| **OCR** | NAVER Cloud CLOVA OCR |
| **Email** | Mailgun |
| **DB 형상관리** | Flyway |
| **API 문서** | Swagger (OpenAPI) |
| **인증** | JWT (HttpOnly Cookie) |

---

## 🏗️ 시스템 아키텍처

### FE — 도메인 중심 피처 기반 아키텍처

```
FE-Shasimi-Six
├── app/                  # 권한별 Route Group (user / admin / auth)
├── components/           # shadcn/ui 기반 공통 컴포넌트
├── features/             # 기능별 독립 비즈니스 로직 및 컴포넌트
│   └── admin / auth / categories / user
├── lib/                  # 전역 유틸리티, 환경 설정
└── services/             # 외부 API 통신 및 데이터 직렬화
```

### BE — 클린 아키텍처 (DDD 기반)

```
com.sashimi.{domain}
├── presentation/         # Controller, Request/Response DTO
├── application/          # Service, UseCase, Command, Port, Policy
├── domain/               # Domain Model, Repository Interface
└── infrastructure/       # Persistence, AI, Email/External
```

### 인증 흐름

```
로그인 요청 → JWT 발급 (Access + Refresh) → API 요청 시 Bearer Token 포함
→ JWT 유효성 검증 → Redis Blacklist 확인 → 비즈니스 로직 수행 → 응답 반환
```

---

## 📂 레포지토리 구조

```
📦 TEAM6-Six-Sashimi
 ├── -BE-Sashimi-Six/   # 백엔드 (Spring Boot / Java)
 └── -FE-Shasimi-Six/   # 프론트엔드 (Next.js / TypeScript)
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

## 🤝 협업 전략

- **요구사항 명세서** — 기능별 정상 흐름·예외 상황을 문서화하여 개발 기준 통일
- **ERD (ERD Cloud)** — 회원, 강의, 수강, 결제, 자격증, AI 추천 도메인 데이터 모델링
- **Figma** — 권한별 UI/UX 흐름 사전 설계
- **REST API 명세** — Request / Response / Status Code 사전 정의로 FE-BE 계약 체결
- **Swagger** — API 문서 자동화 및 인증 API 테스트 지원
- **Flyway** — DB 변경사항 버전 관리 (V1, V2 … 단계별 마이그레이션)
- **Slack** — GitHub PR 알림 연동으로 코드 리뷰 즉시 확인

---

## ✅ 테스트

백엔드 총 **43개 테스트 100% 통과** (Gradle Test)

| 테스트 | 내용 |
|--------|------|
| ArchUnit 계층 의존성 검증 | 클린 아키텍처 규칙 자동 검증 (Domain/Presentation/Infrastructure 간 금지 규칙) |
| Port 계약 테스트 | InMemory 구현체 & JPA+H2 구현체 동일 계약 통과 |
| CertificateCommandServiceTest | OCR API 성공/실패 케이스 검증 |
| PaymentCommandServiceTest | 크레딧 차감 → 수강 등록 트랜잭션 검증 |
| SignupEligibilityPolicyTest | 추천인 코드 정책 단위 검증 |
| EmailVerificationServiceTest | 인증 코드 보안 예외 흐름 검증 |
| UserAccountServiceTest | 탈퇴 시 상태 변경 + 토큰 삭제 흐름 검증 |

---

## 🚀 향후 개선 방향

- 외부 결제 시스템 연동 및 구독 시스템 구현
- 이력서·채용공고 기반 AI 추천 프론트 연동 100% 완료
- 커뮤니티·학습 상호작용 기능 70% 이상 구현
- 기출문제 풀이 및 자동 채점, 모의시험 기능
- D-Day 및 학습 미진행 맞춤형 알림 시스템
- 목표 자격증 기반 단계별 학습 계획 자동 생성
- AI 통합 챗봇 (강의·자격증·이력서·수강 이력 연결)
