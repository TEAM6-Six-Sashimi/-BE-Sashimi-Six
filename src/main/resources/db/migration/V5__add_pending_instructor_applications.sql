-- 강사 신청 대기(PENDING) 더미 데이터 추가
-- 신규 유저 3명 추가 (강사 신청 예정자)
INSERT IGNORE INTO users
(login_id, email, password, name, birth_date, phone, role, status, email_verified, referral_code)
VALUES
    ('pending01', 'pending1@test.com', '$2a$10$pending', '홍길동', '1995-04-11', '010-6666-6666', 'STUDENT', 'ACTIVE', TRUE, 'PND001'),
    ('pending02', 'pending2@test.com', '$2a$10$pending', '이순신', '1993-08-22', '010-7777-7777', 'STUDENT', 'ACTIVE', TRUE, 'PND002'),
    ('pending03', 'pending3@test.com', '$2a$10$pending', '강감찬', '1997-02-05', '010-8888-8888', 'STUDENT', 'ACTIVE', TRUE, 'PND003');

-- 강사 신청 PENDING 데이터 추가
INSERT IGNORE INTO instructor_profiles
(bio, portfolio_url, certification_name, issued_by, approval_status, approved_at, user_id)
VALUES
    ('정보처리기사 자격증 보유. 백엔드 개발 7년 경력으로 실무 중심 강의를 진행합니다.',
     'https://portfolio.example.com/pending1',
     '정보처리기사', '한국산업인력공단',
     'PENDING', NULL,
     (SELECT user_id FROM users WHERE login_id = 'pending01')),

    ('SQLD, 빅데이터분석기사 보유. 데이터 분석 분야 강의 전문입니다.',
     'https://portfolio.example.com/pending2',
     '빅데이터분석기사', '한국데이터산업진흥원',
     'PENDING', NULL,
     (SELECT user_id FROM users WHERE login_id = 'pending02')),

    ('한국사능력검정시험 1급 보유. 역사 관련 강의를 희망합니다.',
     'https://portfolio.example.com/pending3',
     '한국사능력검정시험', '국사편찬위원회',
     'PENDING', NULL,
     (SELECT user_id FROM users WHERE login_id = 'pending03'));
