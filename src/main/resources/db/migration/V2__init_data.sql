INSERT IGNORE INTO users
(login_id, email, password, name, birth_date, phone, role, status, email_verified, referral_code)
VALUES
    ('admin01', 'admin@test.com', '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C', '관리자', '1985-01-01', '010-0000-0000', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN001'),
    ('instructor01', 'instructor1@test.com', '$2a$10$SwKCvl1vtkjOSh0F.KflsOx8eolg1MTZdqUXcfzZQ4piogm8.PosK', '김강사', '1988-03-12', '010-1111-1111', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS001'),
    ('instructor02', 'instructor2@test.com', '$2a$10$SwKCvl1vtkjOSh0F.KflsOx8eolg1MTZdqUXcfzZQ4piogm8.PosK', '이강사', '1990-07-20', '010-2222-2222', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS002'),
    ('student01', 'student1@test.com', '$2a$10$vYwWcwkU/lHpqmL3WERfgOi0yx/OxCkfbtGQHJjIj8IpfTdGptORi', '박학생', '2001-05-14', '010-3333-3333', 'STUDENT', 'ACTIVE', TRUE, 'STD001'),
    ('student02', 'student2@test.com', '$2a$10$vYwWcwkU/lHpqmL3WERfgOi0yx/OxCkfbtGQHJjIj8IpfTdGptORi', '최학생', '2000-10-03', '010-4444-4444', 'STUDENT', 'ACTIVE', TRUE, 'STD002'),
    ('student03', 'student3@test.com', '$2a$10$vYwWcwkU/lHpqmL3WERfgOi0yx/OxCkfbtGQHJjIj8IpfTdGptORi', '정학생', '1999-12-25', '010-5555-5555', 'STUDENT', 'ACTIVE', TRUE, 'STD003');

INSERT IGNORE INTO categories
(main_category_id, name, sub_category, sort_order, is_active)
VALUES
    (1, 'IT·정보통신', '정보처리기사', 1, TRUE),
    (1, 'IT·정보통신', '정보보안기사', 2, TRUE),
    (1, 'IT·정보통신', '네트워크관리사', 3, TRUE),
    (1, 'IT·정보통신', '빅데이터분석기사', 4, TRUE),
    (1, 'IT·정보통신', 'ADsP', 5, TRUE),
    (1, 'IT·정보통신', 'SQLD', 6, TRUE),
    (2, '경영·회계', '전산회계', 7, TRUE),
    (2, '경영·회계', '재경관리사', 8, TRUE),
    (2, '경영·회계', '물류관리사', 9, TRUE),
    (2, '경영·회계', '사회조사분석사', 10, TRUE),
    (2, '경영·회계', '유통관리사', 11, TRUE),
    (3, '디자인', '컴퓨터그래픽스운용기능사', 12, TRUE),
    (3, '디자인', '시각디자인산업기사', 13, TRUE),
    (3, '디자인', '웹디자인기능사', 14, TRUE),
    (3, '디자인', '실내건축기능사', 15, TRUE),
    (4, '건설·안전', '산업안전기사', 16, TRUE),
    (4, '건설·안전', '건설안전기사', 17, TRUE),
    (4, '건설·안전', '소방설비기사', 18, TRUE),
    (4, '건설·안전', '위험물산업기사', 19, TRUE),
    (5, '식품·조리', '조리기능사', 20, TRUE),
    (5, '식품·조리', '제과기능사', 21, TRUE),
    (5, '식품·조리', '제빵기능사', 22, TRUE),
    (5, '식품·조리', '식품기사', 23, TRUE),
    (5, '식품·조리', '영양사', 24, TRUE),
    (6, '부동산·금융', '공인중개사', 25, TRUE),
    (6, '부동산·금융', '주택관리사', 26, TRUE),
    (6, '부동산·금융', '감정평가사', 27, TRUE),
    (7, '어학', '한국어교원자격증', 28, TRUE),
    (7, '어학', '관광통역안내사', 29, TRUE);

INSERT IGNORE INTO ai_prompts
(name, purpose, prompt, version, is_active)
VALUES
    ('이력서 생성 프롬프트 v1', 'RESUME_GENERATION',
     '사용자의 경력과 기술스택을 바탕으로 신입 개발자 이력서 초안을 작성해주세요. 사용자 정보: {userProfile}',
     1, TRUE),
    ('이력서 평가 프롬프트 v1', 'RESUME_EVALUATION',
     '아래 이력서 내용을 BASIC(기본 정보), EDUCATION(학력 사항), CAREER(경력 사항), SKILL(기술 및 자격증) 기준으로 평가해주세요. 이력서 제목: {resumeTitle}, 이력서 내용: {resumeContent}',
     1, TRUE),
    ('채용공고 분석 프롬프트 v1', 'JOB_ANALYSIS',
     '아래 채용공고 내용을 분석해서 직무명, 요구 역량, 추천 자격증, 보완 학습이 필요한 강의 방향을 도출해주세요. 이력서 보유 여부: {resumeBased}, 채용공고 내용: {jobPostingContent}',
     1, TRUE),
    ('강의 추천 프롬프트 v1', 'COURSE_RECOMMENDATION',
     '사용자의 목표, 부족한 기술, 채용공고 요구 역량을 바탕으로 적합한 강의를 추천해주세요. 목표: {targetGoal}, 부족한 기술: {missingSkills}, 요구 역량: {requiredSkills}',
     1, TRUE);

INSERT IGNORE INTO instructor_profiles
(bio, portfolio_url, certification_name, issued_by, approval_status, approved_at, user_id)
VALUES
    ('정보처리기사와 SQLD 자격증 강의를 전문으로 합니다.', 'https://portfolio.example.com/instructor1', '정보처리기사', '한국산업인력공단', 'APPROVED', NOW(), 2),
    ('데이터 분석 자격증과 빅데이터분석기사 강의를 진행합니다.', 'https://portfolio.example.com/instructor2', 'ADsP', '한국데이터산업진흥원', 'APPROVED', NOW(), 3);

INSERT IGNORE INTO credits
(balance, user_id)
VALUES
    (15000, 4),
    (5000, 5),
    (0, 6);
