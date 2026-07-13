-- be2/V3.2.2__reset_course_dummy_data.sql
-- =========================================================
-- 강의 관련 더미 데이터 전체 삭제 + AUTO_INCREMENT 초기화
-- 목적: 정합성 깨진 더미 데이터를 비우고 재시딩 준비
-- 주의: 테이블 구조는 유지되고 행(row)만 삭제됨
-- FK 의존성에 따라 자식 -> 부모 순서로 삭제
-- =========================================================

-- [1단계] 말단 자식 (다른 테이블을 참조만 하는 것들)
DELETE FROM review_reports;         -- reviews 참조
DELETE FROM subscription_payments;  -- orders, payments 참조
DELETE FROM coupon_usages;          -- orders 참조
DELETE FROM cart_items;             -- courses 참조
DELETE FROM roadmap_steps;          -- courses 참조
DELETE FROM course_qna;             -- courses, course_sessions 참조
DELETE FROM learning_progress;      -- course_sessions, courses 참조

-- [2단계] 중간 계층
DELETE FROM reviews;                -- courses 참조 (review_reports 삭제 후)
DELETE FROM enrollments;            -- order_items, courses 참조 (order_items보다 먼저)
DELETE FROM payments;               -- orders 참조 (subscription_payments 삭제 후)
DELETE FROM order_items;            -- orders, courses 참조 (enrollments 삭제 후)

-- [3단계] 상위 부모
DELETE FROM orders;                 -- payments/order_items/coupon_usages 삭제 후
DELETE FROM course_sessions;        -- courses 참조 (qna/learning_progress 삭제 후)

-- [4단계] 최상위 부모
DELETE FROM courses;

-- =========================================================
-- AUTO_INCREMENT 초기화 (비운 테이블은 1부터 다시 시작)
-- course_id 를 1부터 시작하도록 재설정
-- =========================================================
ALTER TABLE courses           AUTO_INCREMENT = 1;
ALTER TABLE course_sessions   AUTO_INCREMENT = 1;
ALTER TABLE enrollments       AUTO_INCREMENT = 1;
ALTER TABLE learning_progress AUTO_INCREMENT = 1;
ALTER TABLE reviews           AUTO_INCREMENT = 1;
ALTER TABLE orders            AUTO_INCREMENT = 1;
ALTER TABLE order_items       AUTO_INCREMENT = 1;
ALTER TABLE payments          AUTO_INCREMENT = 1;
-- be4/V3.4.2__dummy_pending_instructor_application.sql
-- 관리자 API 연동 테스트용 강사 신청 대기(PENDING) 더미 데이터

-- 테스트용 유저 추가 (비밀번호: Test1234! 와 동일한 admin01 해시 재사용)
INSERT IGNORE INTO users
    (login_id, email, password, name, birth_date, phone, role, status, email_verified, referral_code, marketing_consent, email_consent, ai_consent)
VALUES
    ('testinstructor',
     'testinstructor@test.com',
     '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C',
     '김테스트',
     '1992-03-15',
     '010-1234-5678',
     'STUDENT',
     'ACTIVE',
     TRUE,
     'TINST001',
     FALSE, FALSE, FALSE);

-- 강사 신청 PENDING 더미 데이터 (전체 컬럼 포함)
INSERT IGNORE INTO instructor_profiles
    (user_id, bio, motivation_letter, category_id, portfolio_url,
     profile_image_path, resume_file_path, main_careers, approval_status,
     approved_at, created_at, updated_at)
VALUES
    ((SELECT user_id FROM users WHERE login_id = 'testinstructor'),
     '정보처리기사 자격증 보유. 백엔드 개발 5년 경력으로 실무 중심 강의를 진행하고자 합니다.',
     'IT 분야에 대한 깊은 열정과 실무 경험을 바탕으로 수강생들에게 실질적인 도움이 되는 강의를 제공하고 싶습니다. 단순 이론이 아닌 현업에서 바로 사용할 수 있는 내용을 전달하겠습니다.',
     1,
     'https://github.com/testinstructor',
     'instructor-profiles/testinstructor/profile.jpg',
     'instructor-resumes/testinstructor/resume.docx',
     '["Spring Boot 기반 백엔드 개발 (5년)", "AWS 클라우드 인프라 운영 (3년)", "정보처리기사 취득 (2020)"]',
     'PENDING',
     NULL,
     NOW(),
     NOW());

-- 자격증 더미 데이터
INSERT INTO instructor_application_certifications
(instructor_profile_id, certification_name, issued_by, file_path)
VALUES
    ((SELECT instructor_profile_id FROM instructor_profiles WHERE user_id =
                                                                  (SELECT user_id FROM users WHERE login_id = 'testinstructor')),
     '정보처리기사',
     '한국산업인력공단',
     'instructor-certifications/testinstructor/cert_01.jpg');
-- be4/V3.4.5__insert_test_users.sql
-- 테스트 계정 30명 (test01~test30 / 비밀번호: test)
INSERT INTO users (login_id, email, password, name, birth_date, role, status, email_verified, referral_code)
VALUES
    ('test01', 'test01@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트01', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000001'),
    ('test02', 'test02@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트02', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000002'),
    ('test03', 'test03@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트03', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000003'),
    ('test04', 'test04@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트04', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000004'),
    ('test05', 'test05@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트05', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000005'),
    ('test06', 'test06@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트06', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000006'),
    ('test07', 'test07@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트07', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000007'),
    ('test08', 'test08@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트08', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000008'),
    ('test09', 'test09@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트09', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000009'),
    ('test10', 'test10@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트10', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000010'),
    ('test11', 'test11@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트11', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000011'),
    ('test12', 'test12@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트12', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000012'),
    ('test13', 'test13@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트13', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000013'),
    ('test14', 'test14@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트14', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000014'),
    ('test15', 'test15@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트15', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000015'),
    ('test16', 'test16@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트16', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000016'),
    ('test17', 'test17@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트17', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000017'),
    ('test18', 'test18@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트18', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000018'),
    ('test19', 'test19@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트19', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000019'),
    ('test20', 'test20@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트20', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000020'),
    ('test21', 'test21@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트21', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000021'),
    ('test22', 'test22@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트22', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000022'),
    ('test23', 'test23@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트23', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000023'),
    ('test24', 'test24@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트24', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000024'),
    ('test25', 'test25@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트25', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000025'),
    ('test26', 'test26@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트26', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000026'),
    ('test27', 'test27@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트27', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000027'),
    ('test28', 'test28@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트28', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000028'),
    ('test29', 'test29@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트29', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000029'),
    ('test30', 'test30@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트30', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000030');
-- be4/V3.4.6__dummy_instructor_applications.sql
-- 강사 신청 더미 데이터: PENDING 10개 + REJECTED 10개
-- test01~test20 유저 사용 (V3.4.5에서 생성)

-- =====================
-- PENDING (대기) 10개
-- =====================
INSERT INTO instructor_profiles
(user_id, bio, motivation_letter, category_id, portfolio_url,
 profile_image_path, resume_file_path, main_careers, approval_status,
 approved_at, created_at, updated_at)
VALUES
    ((SELECT user_id FROM users WHERE login_id = 'test01'),
     '정보처리기사 자격증 보유. 백엔드 개발 4년 경력으로 실무 중심 강의를 희망합니다.',
     'Spring Boot와 JPA를 활용한 실무 경험을 바탕으로 취업 준비생에게 실질적인 도움이 되고 싶습니다.',
     1, 'https://github.com/test01',
     'instructor-applications/profile/test01.jpg',
     'instructor-applications/resume/test01.docx',
     '["Spring Boot 백엔드 개발 (4년)", "AWS 인프라 운영 (2년)", "정보처리기사 취득 (2021)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test02'),
     '네트워크관리사 2급 보유. 네트워크 및 보안 분야 강의를 희망합니다.',
     '현업 네트워크 엔지니어로서의 경험을 나누고 싶습니다.',
     2, 'https://github.com/test02',
     'instructor-applications/profile/test02.jpg',
     'instructor-applications/resume/test02.docx',
     '["네트워크 인프라 설계 (5년)", "보안 취약점 분석 (3년)", "네트워크관리사 2급 취득 (2020)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test03'),
     '리눅스마스터 1급 보유. 리눅스 서버 관리 강의를 희망합니다.',
     '서버 운영 경험을 통해 실무적인 내용을 전달하고 싶습니다.',
     1, 'https://github.com/test03',
     'instructor-applications/profile/test03.jpg',
     'instructor-applications/resume/test03.docx',
     '["Linux 서버 운영 (6년)", "Docker/Kubernetes 관리 (3년)", "리눅스마스터 1급 취득 (2019)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test04'),
     '정보보안기사 보유. 보안 분야 강의를 희망합니다.',
     '보안 취약점 분석 및 대응 경험을 강의로 전달하고 싶습니다.',
     2, 'https://github.com/test04',
     'instructor-applications/profile/test04.jpg',
     'instructor-applications/resume/test04.docx',
     '["보안 컨설팅 (4년)", "취약점 분석 (3년)", "정보보안기사 취득 (2022)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test05'),
     'SQLD 자격증 보유. 데이터베이스 강의를 희망합니다.',
     'SQL 최적화 및 데이터 모델링 경험을 공유하고 싶습니다.',
     1, 'https://github.com/test05',
     'instructor-applications/profile/test05.jpg',
     'instructor-applications/resume/test05.docx',
     '["데이터베이스 설계 (5년)", "SQL 튜닝 (4년)", "SQLD 취득 (2021)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test06'),
     '빅데이터분석기사 보유. 데이터 분석 강의를 희망합니다.',
     '머신러닝과 데이터 분석 실무 경험을 나누고 싶습니다.',
     2, 'https://github.com/test06',
     'instructor-applications/profile/test06.jpg',
     'instructor-applications/resume/test06.docx',
     '["데이터 분석 (3년)", "Python ML 개발 (2년)", "빅데이터분석기사 취득 (2022)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test07'),
     '정보처리기사 보유. 프론트엔드 강의를 희망합니다.',
     'React와 TypeScript 실무 경험을 기반으로 강의하고 싶습니다.',
     1, 'https://github.com/test07',
     'instructor-applications/profile/test07.jpg',
     'instructor-applications/resume/test07.docx',
     '["React 프론트엔드 개발 (4년)", "TypeScript 적용 (3년)", "정보처리기사 취득 (2020)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test08'),
     'AWS SAA 자격증 보유. 클라우드 강의를 희망합니다.',
     'AWS 클라우드 아키텍처 설계 경험을 공유하고 싶습니다.',
     2, 'https://github.com/test08',
     'instructor-applications/profile/test08.jpg',
     'instructor-applications/resume/test08.docx',
     '["AWS 클라우드 아키텍처 (4년)", "DevOps 운영 (3년)", "AWS SAA 취득 (2021)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test09'),
     '정보처리기사 보유. 알고리즘 및 자료구조 강의를 희망합니다.',
     '코딩테스트 준비 경험을 나누며 취업 준비생을 돕고 싶습니다.',
     1, 'https://github.com/test09',
     'instructor-applications/profile/test09.jpg',
     'instructor-applications/resume/test09.docx',
     '["알고리즘 튜터링 (3년)", "코딩테스트 강의 (2년)", "정보처리기사 취득 (2022)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test10'),
     '전자계산기조직응용기사 보유. 컴퓨터 구조 강의를 희망합니다.',
     '하드웨어와 소프트웨어의 연계를 실무 관점에서 가르치고 싶습니다.',
     2, 'https://github.com/test10',
     'instructor-applications/profile/test10.jpg',
     'instructor-applications/resume/test10.docx',
     '["시스템 아키텍처 설계 (5년)", "임베디드 개발 (3년)", "전자계산기조직응용기사 취득 (2019)"]',
     'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- =====================
-- REJECTED (반려) 10개
-- =====================
INSERT INTO instructor_profiles
(user_id, bio, motivation_letter, category_id, portfolio_url,
 profile_image_path, resume_file_path, main_careers, approval_status,
 rejection_category, rejection_reason, approved_at, created_at, updated_at)
VALUES
    ((SELECT user_id FROM users WHERE login_id = 'test11'),
     '자격증 취득 후 강의 활동을 시작하고 싶습니다.',
     '열심히 가르쳐 보겠습니다.',
     1, 'https://github.com/test11',
     'instructor-applications/profile/test11.jpg',
     'instructor-applications/resume/test11.docx',
     '["프리랜서 개발 (1년)"]',
     'REJECTED', 'INSUFFICIENT_CAREER_PROOF', '경력을 증빙할 수 있는 서류가 부족합니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test12'),
     '개발자 지망생으로 강의를 통해 성장하고 싶습니다.',
     '배우면서 가르치는 방식으로 진행하고 싶습니다.',
     2, 'https://github.com/test12',
     'instructor-applications/profile/test12.jpg',
     'instructor-applications/resume/test12.docx',
     '["개인 프로젝트 (6개월)"]',
     'REJECTED', 'INSUFFICIENT_BASIC_INFO', '자기소개 및 강의 계획이 구체적이지 않습니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test13'),
     '다양한 분야의 강의를 진행하고 싶습니다.',
     '여러 기술을 두루 알고 있습니다.',
     1, 'https://github.com/test13',
     'instructor-applications/profile/test13.jpg',
     'instructor-applications/resume/test13.docx',
     '["다양한 IT 업무 (2년)"]',
     'REJECTED', 'INSUFFICIENT_CAREER_PROOF', '제출된 자격증 정보와 경력이 일치하지 않습니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test14'),
     '부업으로 강의를 진행하고 싶습니다.',
     '시간이 날 때 강의 자료를 준비하겠습니다.',
     2, 'https://github.com/test14',
     'instructor-applications/profile/test14.jpg',
     'instructor-applications/resume/test14.docx',
     '["회사원 (3년)"]',
     'REJECTED', 'INSUFFICIENT_BASIC_INFO', '강의에 대한 구체적인 계획과 의지가 확인되지 않습니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test15'),
     '온라인 강의 경험이 없지만 도전하고 싶습니다.',
     '처음이지만 열심히 하겠습니다.',
     1, 'https://github.com/test15',
     'instructor-applications/profile/test15.jpg',
     'instructor-applications/resume/test15.docx',
     '["IT 관련 아르바이트 (1년)"]',
     'REJECTED', 'UNABLE_TO_VERIFY_IDENTITY', '본인 확인 서류가 제출되지 않았습니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test16'),
     '현재 취업 준비 중이며 강의도 병행하고 싶습니다.',
     '공부한 내용을 강의로 나누겠습니다.',
     2, 'https://github.com/test16',
     'instructor-applications/profile/test16.jpg',
     'instructor-applications/resume/test16.docx',
     '["학원 조교 (6개월)"]',
     'REJECTED', 'INSUFFICIENT_CAREER_PROOF', '강사로서의 전문 경력이 충분하지 않습니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test17'),
     '유튜브 영상을 보고 독학한 내용을 강의하고 싶습니다.',
     '독학으로 익힌 기술을 나누겠습니다.',
     1, 'https://github.com/test17',
     'instructor-applications/profile/test17.jpg',
     'instructor-applications/resume/test17.docx',
     '["독학 프로젝트 (1년)"]',
     'REJECTED', 'INAPPROPRIATE_CAREER_INCLUDED', '이력에 검증되지 않은 경력이 포함되어 있습니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test18'),
     '관련 자격증은 없지만 실무 경험이 있습니다.',
     '현장 경험을 바탕으로 강의하겠습니다.',
     2, 'https://github.com/test18',
     'instructor-applications/profile/test18.jpg',
     'instructor-applications/resume/test18.docx',
     '["소규모 업체 개발 (2년)"]',
     'REJECTED', 'UNABLE_TO_VERIFY_IDENTITY', '제출된 서류만으로는 경력 확인이 어렵습니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test19'),
     '부트캠프 수료 후 강의에 도전하고 싶습니다.',
     '수료 경험을 바탕으로 예비 개발자를 돕고 싶습니다.',
     1, 'https://github.com/test19',
     'instructor-applications/profile/test19.jpg',
     'instructor-applications/resume/test19.docx',
     '["부트캠프 수료 (6개월)", "개인 프로젝트 (3개월)"]',
     'REJECTED', 'INSUFFICIENT_CAREER_PROOF', '실무 경력이 확인되지 않아 반려합니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),

    ((SELECT user_id FROM users WHERE login_id = 'test20'),
     '해외 경력이 있으나 국내 자격증은 없습니다.',
     '해외 경험을 국내 수강생과 나누고 싶습니다.',
     2, 'https://github.com/test20',
     'instructor-applications/profile/test20.jpg',
     'instructor-applications/resume/test20.docx',
     '["해외 IT 기업 근무 (3년)"]',
     'REJECTED', 'UNABLE_TO_VERIFY_IDENTITY', '해외 경력 증빙 서류를 확인할 수 없습니다.',
     NULL, DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY));
-- be4/V3.4.7__update_instructor_main_careers.sql
-- instructor01(김강사), instructor02(이강사) main_careers 더미 데이터 추가
-- V1 INSERT 당시 main_careers 컬럼이 없었기 때문에 NULL 상태로 남겨진 것을 보완

UPDATE instructor_profiles
SET main_careers = JSON_ARRAY(
        '(주)스마트이노베이션 백엔드 개발자 (2015.03 ~ 2021.06)',
        '프리랜서 IT 강의 및 멘토링 (2021.07 ~ 2023.12)',
        '정보처리기사 취득 (2014)',
        'SQLD 취득 (2016)'
                   )
WHERE user_id = (SELECT user_id FROM users WHERE login_id = 'instructor01');

UPDATE instructor_profiles
SET main_careers = JSON_ARRAY(
        '(주)데이터허브 데이터 분석가 (2016.02 ~ 2022.08)',
        '빅데이터 컨설팅 프리랜서 (2022.09 ~ 2024.06)',
        'ADsP 취득 (2015)',
        '빅데이터분석기사 취득 (2021)'
                   )
WHERE user_id = (SELECT user_id FROM users WHERE login_id = 'instructor02');
-- be5/V3.5.6__dummy_course_reviews.sql
-- k6 부하테스트 및 시연용 수강평 더미 데이터
-- course_id=1: test031~test060 (30개, Redis 캐싱 테스트용)
-- course_id=2~10: test01~test10 (각 3개)
-- 신고 유형 포함: 광고성 스팸, 허위정보, 욕설/비방, 도배성

-- =====================================================
-- course_id=1 (정보처리기사 필기 완전정복) - 30개
-- =====================================================
INSERT IGNORE INTO reviews (rating, content, status, created_at, user_id, course_id)
WITH RECURSIVE seq AS (
    SELECT 31 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 60
),
contents (idx, rating, content) AS (
    SELECT 1,  5, '정말 유익한 강의입니다! 실무에 바로 적용할 수 있어서 좋았어요.'
    UNION ALL SELECT 2,  5, '설명이 쉽고 명확해서 이해하기 편했습니다. 강력 추천합니다!'
    UNION ALL SELECT 3,  4, '기초부터 차근차근 알려주셔서 좋았습니다. 핵심 정리가 잘 돼있어요.'
    UNION ALL SELECT 4,  4, '실무에 바로 적용할 수 있는 내용이라 만족스러웠습니다.'
    UNION ALL SELECT 5,  5, '강의 구성이 체계적이고 내용이 알찼습니다. 합격에 큰 도움이 됐어요.'
    UNION ALL SELECT 6,  1, '이 강의 별로예요 ㅋㅋ 그냥 유튜브 무료강의나 들으세요. 돈낭비임'  -- 허위정보성 비방
    UNION ALL SELECT 7,  5, '이 가격에 이 퀄리티라니 정말 만족합니다. 강사님 최고예요!'
    UNION ALL SELECT 8,  5, '좋아요좋아요좋아요좋아요좋아요좋아요좋아요좋아요좋아요좋아요'  -- 도배성
    UNION ALL SELECT 9,  5, '핵심만 쏙쏙 뽑아서 설명해주셔서 효율적으로 학습했어요.'
    UNION ALL SELECT 10, 5, '취업 준비 중인데 이 강의 덕분에 자격증 취득했습니다! 감사합니다.'
)
SELECT
    c.rating,
    c.content,
    'ACTIVE',
    DATE_SUB(NOW(), INTERVAL (60 - seq.n) DAY),
    u.user_id,
    1
FROM seq
         JOIN users u ON u.login_id = CONCAT('test', LPAD(seq.n, 3, '0'))
         JOIN contents c ON c.idx = ((seq.n - 31) % 10) + 1;

-- =====================================================
-- course_id=2~10 (각 강의별 3개씩, 신고유형 혼합)
-- =====================================================
INSERT IGNORE INTO reviews (rating, content, status, created_at, user_id, course_id)
-- course 2
SELECT 5, '강의 내용이 알차고 유익합니다. 추천해요!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY), user_id, 2 FROM users WHERE login_id = 'test01'
UNION ALL SELECT 1, '강사가 설명을 너무 못해요. 환불하고 싶습니다. 최악입니다!!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 5 DAY), user_id, 2 FROM users WHERE login_id = 'test02'  -- 비방성
UNION ALL SELECT 5, '합격에 정말 큰 도움이 됐습니다. 감사합니다!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 18 DAY), user_id, 2 FROM users WHERE login_id = 'test03'

-- course 3
UNION ALL SELECT 4, '강의 구성이 체계적이고 좋았습니다.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY), user_id, 3 FROM users WHERE login_id = 'test04'
UNION ALL SELECT 5, '처음 접하는 분들도 쉽게 따라올 수 있는 강의입니다.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 19 DAY), user_id, 3 FROM users WHERE login_id = 'test05'
UNION ALL SELECT 5, '★★★ 자격증 최저가 보장! www.fakecert.com 에서 더 싸게 구매하세요 ★★★', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 1 DAY), user_id, 3 FROM users WHERE login_id = 'test06'  -- 광고성 스팸

-- course 4
UNION ALL SELECT 5, '정말 만족스러운 강의입니다. 다음 강의도 수강할 예정이에요!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY), user_id, 4 FROM users WHERE login_id = 'test07'
UNION ALL SELECT 4, '강사님의 열정이 느껴지는 강의입니다.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 19 DAY), user_id, 4 FROM users WHERE login_id = 'test08'
UNION ALL SELECT 3, '보통이에요 보통이에요 보통이에요 보통이에요 보통이에요 보통이에요', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 2 DAY), user_id, 4 FROM users WHERE login_id = 'test09'  -- 도배성

-- course 5
UNION ALL SELECT 5, '이 강의 덕분에 합격했습니다! 강력 추천합니다.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY), user_id, 5 FROM users WHERE login_id = 'test10'
UNION ALL SELECT 4, '실전 문제 풀이가 특히 도움이 많이 됐어요.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 19 DAY), user_id, 5 FROM users WHERE login_id = 'test01'
UNION ALL SELECT 5, '강의 자료도 충실하고 설명도 너무 좋아요.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 18 DAY), user_id, 5 FROM users WHERE login_id = 'test02'

-- course 6
UNION ALL SELECT 4, '기초부터 탄탄하게 배울 수 있는 강의입니다.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY), user_id, 6 FROM users WHERE login_id = 'test03'
UNION ALL SELECT 1, '이 강사 자격증도 없으면서 가르치는 거 맞죠? 사기꾼 같음', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 3 DAY), user_id, 6 FROM users WHERE login_id = 'test04'  -- 허위정보 + 비방
UNION ALL SELECT 4, '설명이 명확하고 예시가 풍부합니다.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 18 DAY), user_id, 6 FROM users WHERE login_id = 'test05'

-- course 7
UNION ALL SELECT 5, '정말 훌륭한 강의입니다. 강사님 덕분에 합격했어요!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY), user_id, 7 FROM users WHERE login_id = 'test06'
UNION ALL SELECT 4, '핵심 요약이 잘 돼있어서 복습하기 편합니다.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 19 DAY), user_id, 7 FROM users WHERE login_id = 'test07'
UNION ALL SELECT 5, '체계적인 커리큘럼으로 단기 합격했습니다!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 18 DAY), user_id, 7 FROM users WHERE login_id = 'test08'

-- course 8
UNION ALL SELECT 4, '강의 내용이 실전과 잘 연계되어 있습니다.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY), user_id, 8 FROM users WHERE login_id = 'test09'
UNION ALL SELECT 5, '이 강의 수강 후 바로 합격했습니다. 추천합니다!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 19 DAY), user_id, 8 FROM users WHERE login_id = 'test10'
UNION ALL SELECT 4, '꼼꼼한 설명 덕분에 어려운 내용도 이해할 수 있었어요.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 18 DAY), user_id, 8 FROM users WHERE login_id = 'test01'

-- course 9
UNION ALL SELECT 5, '강의 퀄리티가 정말 높습니다. 매우 만족스러워요!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY), user_id, 9 FROM users WHERE login_id = 'test02'
UNION ALL SELECT 4, '입문자도 쉽게 따라갈 수 있는 강의입니다.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 19 DAY), user_id, 9 FROM users WHERE login_id = 'test03'
UNION ALL SELECT 5, '강의 덕분에 자신감이 생겼습니다. 정말 감사해요!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 18 DAY), user_id, 9 FROM users WHERE login_id = 'test04'

-- course 10
UNION ALL SELECT 4, '설명이 쉽고 이해가 빠릅니다. 만족스러운 강의예요.', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY), user_id, 10 FROM users WHERE login_id = 'test05'
UNION ALL SELECT 5, '이 분야 최고의 강의라고 생각합니다!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 19 DAY), user_id, 10 FROM users WHERE login_id = 'test06'
UNION ALL SELECT 4, '강의 구성과 난이도가 적절합니다. 추천해요!', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 18 DAY), user_id, 10 FROM users WHERE login_id = 'test07';
