INSERT INTO users
(login_id, email, password, name, phone, role, status, email_verified, referral_code)
VALUES
    ('admin01', 'admin@test.com', '$2a$10$admin', '관리재', '010-0000-0000', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN001'),
    ('instructor01', 'instructor1@test.com', '$2a$10$instructor', '김강사', '010-1111-1111', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS001'),
    ('instructor02', 'instructor2@test.com', '$2a$10$instructor', '이강사', '010-2222-2222', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS002'),
    ('student01', 'student1@test.com', '$2a$10$student', '박학생', '010-3333-3333', 'STUDENT', 'ACTIVE', TRUE, 'STD001'),
    ('student02', 'student2@test.com', '$2a$10$student', '최학생', '010-4444-4444', 'STUDENT', 'ACTIVE', TRUE, 'STD002'),
    ('student03', 'student3@test.com', '$2a$10$student', '정학생', '010-5555-5555', 'STUDENT', 'ACTIVE', TRUE, 'STD003');

INSERT INTO categories
(name, type, sort_order, is_active)
VALUES
    ('백엔드', 'COURSE', 1, TRUE),
    ('프론트엔드', 'COURSE', 2, TRUE),
    ('데이터분석', 'COURSE', 3, TRUE),
    ('AI/머신러닝', 'COURSE', 4, TRUE),
    ('자격증', 'CERTIFICATION', 5, TRUE);

INSERT INTO courses
(title, description, price, difficulty, thumbnail, total_duration, status, rating_avg, review_count, student_count, approved_at, category_id, instructor_id)
VALUES
    ('Spring Boot 입문', 'Spring Boot로 REST API 서버를 만드는 입문 강의입니다.', 59000, 'BEGINNER', '/images/spring-basic.png', 10800, 'APPROVED', 4.8, 2, 3, NOW(), 1, 2),
    ('JPA 실전 강의', 'JPA 연관관계와 쿼리 최적화를 배우는 강의입니다.', 79000, 'INTERMEDIATE', '/images/jpa.png', 14400, 'APPROVED', 4.5, 1, 2, NOW(), 1, 2),
    ('React 기본부터 프로젝트까지', 'React 기반 프론트엔드 개발 강의입니다.', 69000, 'BEGINNER', '/images/react.png', 12600, 'APPROVED', 4.7, 1, 1, NOW(), 2, 3),
    ('Python 데이터분석', 'Pandas와 Matplotlib을 활용한 데이터분석 강의입니다.', 49000, 'BEGINNER', '/images/python-data.png', 9000, 'APPROVED', 0.0, 0, 0, NOW(), 3, 3);

INSERT INTO course_sessions
(session_uid, title, video_url, duration_seconds, session_order, is_preview, course_id)
VALUES
    (UUID(), 'Spring Boot 소개', '/videos/spring/01.mp4', 1200, 1, TRUE, 1),
    (UUID(), '프로젝트 생성과 구조 이해', '/videos/spring/02.mp4', 1800, 2, FALSE, 1),
    (UUID(), 'Controller와 REST API', '/videos/spring/03.mp4', 2400, 3, FALSE, 1),

    (UUID(), 'JPA 소개', '/videos/jpa/01.mp4', 1500, 1, TRUE, 2),
    (UUID(), 'Entity 매핑', '/videos/jpa/02.mp4', 2700, 2, FALSE, 2),

    (UUID(), 'React 컴포넌트 이해', '/videos/react/01.mp4', 1600, 1, TRUE, 3),
    (UUID(), 'State와 Props', '/videos/react/02.mp4', 2200, 2, FALSE, 3),

    (UUID(), 'Pandas 기본', '/videos/data/01.mp4', 1800, 1, TRUE, 4),
    (UUID(), '데이터 시각화', '/videos/data/02.mp4', 2100, 2, FALSE, 4);

INSERT INTO cart_items
(price, selected, user_id, course_id)
VALUES
    (59000, TRUE, 4, 1),
    (79000, TRUE, 4, 2),
    (69000, TRUE, 5, 3);

INSERT INTO orders
(order_no, total_amount, discount_amount, final_amount, status, user_id)
VALUES
    ('ORD-20260521-0001', 138000, 10000, 128000, 'PAID', 4),
    ('ORD-20260521-0002', 69000, 0, 69000, 'PAID', 5),
    ('ORD-20260521-0003', 59000, 0, 59000, 'PENDING', 6);

INSERT INTO order_items
(course_title, price, discount_amount, final_price, order_id, course_id)
VALUES
    ('Spring Boot 입문', 59000, 5000, 54000, 1, 1),
    ('JPA 실전 강의', 79000, 5000, 74000, 1, 2),
    ('React 기본부터 프로젝트까지', 69000, 0, 69000, 2, 3),
    ('Spring Boot 입문', 59000, 0, 59000, 3, 1);

INSERT INTO payments
(amount, status, paid_at, order_id, user_id)
VALUES
    (128000, 'PAID', NOW(), 1, 4),
    (69000, 'PAID', NOW(), 2, 5),
    (59000, 'READY', NULL, 3, 6);

INSERT INTO enrollments
(enrollment_type, progress_rate, is_completed, enrolled_at, completed_at, order_item_id, course_id, user_id)
VALUES
    ('PAID', 66.67, FALSE, NOW(), NULL, 1, 1, 4),
    ('PAID', 20.00, FALSE, NOW(), NULL, 2, 2, 4),
    ('PAID', 100.00, TRUE, NOW(), NOW(), 3, 3, 5);

INSERT INTO learning_progress
(watched_seconds, last_position_seconds, progress_rate, is_completed, last_watched_at, session_id, course_id, user_id)
VALUES
    (1200, 1200, 100.00, TRUE, NOW(), 1, 1, 4),
    (1800, 1800, 100.00, TRUE, NOW(), 2, 1, 4),
    (900, 900, 37.50, FALSE, NOW(), 3, 1, 4),

    (1500, 1500, 100.00, TRUE, NOW(), 4, 2, 4),
    (300, 300, 11.11, FALSE, NOW(), 5, 2, 4),

    (1600, 1600, 100.00, TRUE, NOW(), 6, 3, 5),
    (2200, 2200, 100.00, TRUE, NOW(), 7, 3, 5);

INSERT INTO reviews
(rating, content, status, user_id, course_id)
VALUES
    (5, 'Spring Boot 입문자가 듣기 좋았습니다.', 'ACTIVE', 4, 1),
    (4, '설명이 친절하고 실습이 많아서 좋았습니다.', 'ACTIVE', 5, 1),
    (5, 'React 기본 개념을 잡기에 좋았습니다.', 'ACTIVE', 5, 3);

INSERT INTO review_replies
(content, review_id, user_id)
VALUES
    ('좋은 리뷰 감사합니다. 다음 강의도 열심히 준비하겠습니다.', 1, 2),
    ('수강해주셔서 감사합니다!', 3, 3);

INSERT INTO course_qna
(title, content, status, course_id, session_id, user_id)
VALUES
    ('Controller와 Service 차이가 궁금합니다.', 'Controller와 Service를 왜 나누는지 잘 모르겠습니다.', 'WAITING', 1, 3, 4),
    ('JPA Entity 수정 질문입니다.', 'Entity 필드명을 바꿨을 때 DB도 자동으로 바뀌나요?', 'ANSWERED', 2, 5, 4);

INSERT INTO course_announcements
(title, content, is_pinned, course_id, user_id)
VALUES
    ('Spring Boot 강의 자료 안내', '섹션별 실습 코드는 첨부파일에서 확인 가능합니다.', TRUE, 1, 2),
    ('React 실습 환경 안내', 'Node.js LTS 버전을 설치해주세요.', TRUE, 3, 3);

INSERT INTO coupons
(code, name, discount_type, discount_value, min_order_amount, started_at, expired_at, is_active)
VALUES
    ('WELCOME10000', '신규 가입 1만원 할인', 'AMOUNT', 10000, 50000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE),
    ('SPRING20', '봄맞이 20% 할인', 'PERCENT', 20, 30000, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), TRUE),
    ('BACKEND5000', '백엔드 강의 5천원 할인', 'AMOUNT', 5000, 30000, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE);

INSERT INTO coupon_usages
(discount_amount, user_id, coupon_id, order_id)
VALUES
    (10000, 4, 1, 1);

INSERT INTO subscriptions
(type, status, price, started_at, expired_at, next_billing_at, auto_renew, user_id)
VALUES
    ('PREMIUM', 'ACTIVE', 19900, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), DATE_ADD(NOW(), INTERVAL 1 MONTH), TRUE, 4),
    ('BASIC', 'ACTIVE', 9900, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), DATE_ADD(NOW(), INTERVAL 1 MONTH), TRUE, 5),
    ('PRO', 'CANCELLED', 29900, DATE_SUB(NOW(), INTERVAL 2 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH), NULL, FALSE, 6);

INSERT INTO credits
(balance, user_id)
VALUES
    (15000, 4),
    (5000, 5),
    (0, 6);

-- 더미 데이터
INSERT INTO instructor_profiles
(bio, career, certifications, portfolio_url, approval_status, approved_at, user_id)
VALUES
    ('백엔드 개발과 Spring Boot 강의를 전문으로 합니다.', '백엔드 개발 7년, 교육 3년', '정보처리기사, SQLD', 'https://portfolio.example.com/instructor1', 'APPROVED', NOW(), 2),
    ('프론트엔드와 데이터 분석 강의를 진행합니다.', '프론트엔드 개발 5년, 데이터 분석 프로젝트 다수', 'ADsP, 빅데이터분석기사', 'https://portfolio.example.com/instructor2', 'APPROVED', NOW(), 3);

INSERT INTO instructor_bank_accounts
(bank_name, account_number, account_holder, is_primary, verification_status, user_id)
VALUES
    ('신한은행', '110-123-456789', '김강사', TRUE, 'VERIFIED', 2),
    ('국민은행', '123456-01-123456', '이강사', TRUE, 'VERIFIED', 3);

INSERT INTO settlements
(period_start, period_end, total_sales, commission_amount, settlement_amount, status, paid_at, approved_by, user_id, bank_account_id)
VALUES
    ('2026-05-01', '2026-05-31', 133000, 26600, 106400, 'PAID', NOW(), 1, 2, 1),
    ('2026-05-01', '2026-05-31', 69000, 13800, 55200, 'APPROVED', NULL, 1, 3, 2);

INSERT INTO notification_settings
(qna_notification, payment_notification, marketing_notification, email_enabled, push_enabled, user_id)
VALUES
    (TRUE, TRUE, FALSE, TRUE, TRUE, 4),
    (TRUE, TRUE, TRUE, TRUE, TRUE, 5),
    (TRUE, FALSE, FALSE, TRUE, FALSE, 6),
    (TRUE, TRUE, FALSE, TRUE, TRUE, 2),
    (TRUE, TRUE, FALSE, TRUE, TRUE, 3);

INSERT INTO notifications
(type, title, content, link_url, is_read, user_id)
VALUES
    ('PAYMENT', '결제가 완료되었습니다.', 'Spring Boot 입문 외 1개 강의 결제가 완료되었습니다.', '/orders/1', FALSE, 4),
    ('COURSE', '수강이 시작되었습니다.', 'Spring Boot 입문 강의를 바로 수강할 수 있습니다.', '/courses/1', FALSE, 4),
    ('QNA', '새 질문이 등록되었습니다.', '수강생이 강의에 질문을 남겼습니다.', '/courses/1/qna', FALSE, 2),
    ('SYSTEM', '서비스 점검 안내', '금일 새벽 2시부터 3시까지 서비스 점검이 예정되어 있습니다.', '/notices', TRUE, 5);

INSERT INTO email_verifications
(target_email, code, purpose, is_verified, expired_at, verified_at, user_id)
VALUES
    ('student1@test.com', '123456', 'SIGNUP', TRUE, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), 4),
    ('newuser@test.com', '654321', 'SIGNUP', FALSE, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NULL, NULL),
    ('student2@test.com', '777888', 'PASSWORD_RESET', FALSE, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NULL, 5);

INSERT INTO ai_prompts
(name, purpose, prompt, version, is_active)
VALUES
    ('이력서 생성 프롬프트 v1', 'RESUME_GENERATION', '사용자의 경력과 기술스택을 바탕으로 신입 개발자 이력서를 작성한다.', 1, TRUE),
    ('이력서 평가 프롬프트 v1', 'RESUME_EVALUATION', '채용공고와 이력서를 비교하여 강점, 약점, 개선점을 평가한다.', 1, TRUE),
    ('채용공고 분석 프롬프트 v1', 'JOB_ANALYSIS', '채용공고에서 핵심 요구 기술과 우대사항을 추출한다.', 1, TRUE),
    ('강의 추천 프롬프트 v1', 'COURSE_RECOMMENDATION', '사용자의 목표와 부족한 기술을 바탕으로 적합한 강의를 추천한다.', 1, TRUE);

INSERT INTO resumes
(title, template_type, content, is_default, user_id)
VALUES
    (
        '백엔드 신입 개발자 이력서',
        'BASIC',
        JSON_OBJECT(
                'name', '박학생',
                'position', 'Backend Developer',
                'skills', JSON_ARRAY('Java', 'Spring Boot', 'MySQL'),
                'projects', JSON_ARRAY('LMS 프로젝트', '의료 AI 상담 프로젝트'),
                'summary', 'Spring Boot와 REST API 개발 경험이 있는 신입 백엔드 개발자입니다.'
        ),
        TRUE,
        4
    ),
    (
        '프론트엔드 지원용 이력서',
        'MODERN',
        JSON_OBJECT(
                'name', '최학생',
                'position', 'Frontend Developer',
                'skills', JSON_ARRAY('React', 'JavaScript', 'HTML', 'CSS'),
                'projects', JSON_ARRAY('강의 플랫폼 UI 개발'),
                'summary', 'React 기반 사용자 중심 UI 구현에 관심이 있습니다.'
        ),
        TRUE,
        5
    );

INSERT INTO job_postings
(source_url, company_name, job_title, raw_content, ai_summary, analysis_status, analysis_at, user_id, prompt_id)
VALUES
    (
        'https://careers.example.com/backend-junior',
        '테크스타트업A',
        '신입 백엔드 개발자',
        'Java, Spring Boot, JPA, MySQL 기반 서비스 개발 경험을 우대합니다.',
        'Java/Spring Boot/JPA/MySQL 역량이 중요한 백엔드 신입 공고입니다.',
        'COMPLETED',
        NOW(),
        4,
        3
    ),
    (
        'https://careers.example.com/frontend-junior',
        '디지털서비스B',
        '프론트엔드 개발자',
        'React, JavaScript, REST API 연동 경험을 요구합니다.',
        'React와 JavaScript 기반 프론트엔드 역량이 필요한 공고입니다.',
        'COMPLETED',
        NOW(),
        5,
        3
    );

INSERT INTO skills
(name, category, description)
VALUES
    ('Java', 'Backend', '객체지향 프로그래밍 언어'),
    ('Spring Boot', 'Backend', 'Java 기반 웹 애플리케이션 프레임워크'),
    ('JPA', 'Backend', 'Java ORM 기술'),
    ('MySQL', 'Database', '관계형 데이터베이스'),
    ('React', 'Frontend', '컴포넌트 기반 프론트엔드 라이브러리'),
    ('JavaScript', 'Frontend', '웹 프론트엔드 핵심 언어'),
    ('Python', 'Data', '데이터 분석 및 AI 개발에 활용되는 언어'),
    ('Pandas', 'Data', 'Python 데이터 분석 라이브러리'),
    ('Machine Learning', 'AI', '데이터 기반 예측 모델링 기술');

INSERT INTO job_required_skills
(importance, required_level, extracted_text, job_posting_id, skill_id)
VALUES
    ('REQUIRED', '기초 이상', 'Java 기반 개발 경험', 1, 1),
    ('REQUIRED', '기초 이상', 'Spring Boot 기반 REST API 개발', 1, 2),
    ('HIGH', '기초 이상', 'JPA 사용 경험 우대', 1, 3),
    ('HIGH', '기초 이상', 'MySQL 사용 경험', 1, 4),
    ('REQUIRED', '기초 이상', 'React 기반 화면 개발', 2, 5),
    ('REQUIRED', '기초 이상', 'JavaScript 활용 능력', 2, 6);

INSERT INTO course_skills
(skill_level, skill_id, course_id)
VALUES
    ('BASIC', 1, 1),
    ('BASIC', 2, 1),
    ('INTERMEDIATE', 3, 2),
    ('INTERMEDIATE', 4, 2),
    ('BASIC', 5, 3),
    ('BASIC', 6, 3),
    ('BASIC', 7, 4),
    ('BASIC', 8, 4);

INSERT INTO ai_resume_evaluations
(overall_score, strengths, weaknesses, suggestions, ai_result, resume_id, job_posting_id, prompt_id)
VALUES
    (
        82.50,
        'Spring Boot 프로젝트 경험과 MySQL 사용 경험이 공고와 잘 맞습니다.',
        'JPA 실무 경험에 대한 설명이 부족합니다.',
        '프로젝트에서 Entity 설계와 연관관계 매핑 경험을 구체적으로 작성하면 좋습니다.',
        JSON_OBJECT(
                'matchRate', 82.5,
                'matchedSkills', JSON_ARRAY('Java', 'Spring Boot', 'MySQL'),
                'missingSkills', JSON_ARRAY('JPA 심화')
        ),
        1,
        1,
        2
    ),
    (
        78.00,
        'React와 JavaScript 기술스택이 공고와 잘 맞습니다.',
        'REST API 연동 경험 설명이 부족합니다.',
        '백엔드 API와 연동한 화면 구현 경험을 구체적으로 추가하면 좋습니다.',
        JSON_OBJECT(
                'matchRate', 78,
                'matchedSkills', JSON_ARRAY('React', 'JavaScript'),
                'missingSkills', JSON_ARRAY('REST API 연동 경험')
        ),
        2,
        2,
        2
    );

INSERT INTO ai_recommendations
(source_type, source_id, recommended_type, recommended_id, score, reason, ai_result, prompt_id, user_id)
VALUES
    (
        'RESUME',
        1,
        'COURSE',
        2,
        91.00,
        '백엔드 공고 대비 JPA 역량 보완이 필요하여 JPA 실전 강의를 추천합니다.',
        JSON_OBJECT(
                'reasonType', 'SKILL_GAP',
                'missingSkill', 'JPA',
                'recommendedCourse', 'JPA 실전 강의'
        ),
        4,
        4
    ),
    (
        'JOB_POSTING',
        2,
        'COURSE',
        3,
        88.00,
        '프론트엔드 공고 대비 React 프로젝트 경험 강화를 위해 추천합니다.',
        JSON_OBJECT(
                'reasonType', 'JOB_MATCH',
                'targetSkill', 'React',
                'recommendedCourse', 'React 기본부터 프로젝트까지'
        ),
        4,
        5
    );

INSERT INTO certifications
(name, description, level, issuing_organization, pass_rate, employment_rate, external_url, category_id)
VALUES
    ('정보처리기사', '소프트웨어 개발 및 정보시스템 구축 역량을 평가하는 국가기술자격입니다.', '기사', '한국산업인력공단', 55.20, 68.50, 'https://www.q-net.or.kr', 5),
    ('SQLD', 'SQL 활용 능력과 데이터 모델링 이해도를 평가하는 자격증입니다.', '민간자격', '한국데이터산업진흥원', 45.30, 61.00, 'https://www.dataq.or.kr', 5),
    ('ADsP', '데이터 분석 기획과 기본 분석 역량을 평가하는 자격증입니다.', '민간자격', '한국데이터산업진흥원', 52.70, 58.40, 'https://www.dataq.or.kr', 5);

INSERT INTO external_exam_links
(title, provider_name, external_url, description, certification_id)
VALUES
    ('정보처리기사 시험 접수', 'Q-Net', 'https://www.q-net.or.kr', '정보처리기사 원서접수 및 시험일정 확인', 1),
    ('SQLD 시험 안내', 'DataQ', 'https://www.dataq.or.kr', 'SQLD 시험 일정 및 접수 안내', 2),
    ('ADsP 시험 안내', 'DataQ', 'https://www.dataq.or.kr', 'ADsP 시험 일정 및 접수 안내', 3);

INSERT INTO roadmaps
(goal_type, title, target_name, due_date, created_by_ai, status, user_id)
VALUES
    ('CERTIFICATION', '정보처리기사 합격 로드맵', '정보처리기사', '2026-08-31', TRUE, 'ACTIVE', 4),
    ('JOB', '백엔드 신입 취업 준비 로드맵', '백엔드 개발자', '2026-09-30', TRUE, 'ACTIVE', 4),
    ('COURSE', 'React 기초 완주 로드맵', 'React 기본부터 프로젝트까지', '2026-06-30', FALSE, 'ACTIVE', 5);

INSERT INTO roadmap_steps
(title, description, step_order, status, course_id, roadmap_id, certification_id)
VALUES
    ('자격증 시험 정보 확인', '시험 일정, 응시 조건, 과목을 확인합니다.', 1, 'DONE', NULL, 1, 1),
    ('기초 이론 학습', '소프트웨어 공학, 데이터베이스, 운영체제 기초를 학습합니다.', 2, 'IN_PROGRESS', NULL, 1, 1),
    ('기출문제 풀이', '최근 5개년 기출문제를 반복 풀이합니다.', 3, 'TODO', NULL, 1, 1),

    ('Spring Boot 강의 수강', '백엔드 기본기를 위해 Spring Boot 입문 강의를 수강합니다.', 1, 'DONE', 1, 2, NULL),
    ('JPA 심화 학습', 'JPA 실전 강의를 통해 연관관계와 쿼리 최적화를 학습합니다.', 2, 'IN_PROGRESS', 2, 2, NULL),
    ('이력서 개선', 'AI 평가 결과를 바탕으로 프로젝트 경험을 보완합니다.', 3, 'TODO', NULL, 2, NULL),

    ('React 기본 학습', '컴포넌트, props, state를 학습합니다.', 1, 'IN_PROGRESS', 3, 3, NULL),
    ('미니 프로젝트 구현', '간단한 강의 목록 페이지를 구현합니다.', 2, 'TODO', 3, 3, NULL);

INSERT INTO inquiries
(category, title, content, status, user_id)
VALUES
    ('결제', '결제 후 강의가 보이지 않습니다.', '결제는 완료되었는데 내 강의실에 강의가 표시되지 않습니다.', 'ANSWERED', 4),
    ('강의', '강의 자료 다운로드 오류', '첨부파일 다운로드 버튼을 눌러도 반응이 없습니다.', 'WAITING', 5);

INSERT INTO inquiry_replies
(content, inquiry_id, user_id)
VALUES
    ('확인 결과 수강 등록이 지연되었습니다. 현재 정상 처리되었습니다.', 1, 1);

INSERT INTO system_notices
(title, content, is_pinned, status, user_id)
VALUES
    ('서비스 정식 오픈 안내', '식스사시미 LMS 서비스가 정식 오픈되었습니다.', TRUE, 'PUBLISHED', 1),
    ('개인정보처리방침 개정 안내', '개인정보처리방침이 2026년 6월 1일부터 개정됩니다.', FALSE, 'PUBLISHED', 1);

INSERT INTO bookmarks
(timestamp_seconds, memo, user_id, session_id)
VALUES
    (520, 'Controller 설명 다시 보기', 4, 3),
    (900, 'JPA Entity 매핑 중요', 4, 5),
    (300, 'React 컴포넌트 개념 복습', 5, 6);

INSERT INTO lecture_notes
(title, content, session_id, user_id)
VALUES
    ('Controller 정리', 'Controller는 요청을 받고 Service에 비즈니스 로직 처리를 위임한다.', 3, 4),
    ('JPA Entity 정리', 'Entity는 DB 테이블과 매핑되는 객체이다.', 5, 4),
    ('React State 정리', 'State는 컴포넌트 내부에서 관리되는 동적인 데이터이다.', 7, 5);

INSERT INTO reports
(target_type, target_id, reason, status, processed_at, user_id)
VALUES
    ('REVIEW', 2, '부적절한 표현이 포함되어 있습니다.', 'PENDING', NULL, 6),
    ('QNA', 1, '질문 내용이 강의와 무관합니다.', 'REJECTED', NOW(), 2);

INSERT INTO admin_logs
(action_type, target_type, target_id, description, user_id)
VALUES
    ('APPROVE_COURSE', 'COURSE', 1, 'Spring Boot 입문 강의를 승인했습니다.', 1),
    ('ANSWER_INQUIRY', 'INQUIRY', 1, '결제 문의에 답변했습니다.', 1),
    ('REJECT_REPORT', 'REPORT', 2, '신고 내용을 검토 후 반려했습니다.', 1);

INSERT INTO user_activity_logs
(action_type, target_type, target_id, ip_address, user_agent, user_id)
VALUES
    ('LOGIN', 'USER', 4, '127.0.0.1', 'Mozilla/5.0', 4),
    ('VIEW_COURSE', 'COURSE', 1, '127.0.0.1', 'Mozilla/5.0', 4),
    ('WATCH_SESSION', 'SESSION', 3, '127.0.0.1', 'Mozilla/5.0', 4),
    ('WRITE_REVIEW', 'COURSE', 1, '127.0.0.1', 'Mozilla/5.0', 5);

INSERT INTO d_days
(title, target_date, category, user_id)
VALUES
    ('정보처리기사 필기시험', '2026-08-31', 'CERTIFICATION', 4),
    ('백엔드 신입 지원 마감', '2026-09-30', 'JOB', 4),
    ('React 강의 완강 목표', '2026-06-30', 'COURSE', 5);