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
