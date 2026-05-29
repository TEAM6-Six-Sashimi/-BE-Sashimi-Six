INSERT IGNORE INTO users
(login_id, email, password, name, birth_date, phone, role, status, email_verified, referral_code)
VALUES
    ('admin01', 'admin@test.com', '$2a$10$admin', '관리자', '1985-01-01', '010-0000-0000', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN001'),
    ('instructor01', 'instructor1@test.com', '$2a$10$SwKCvl1vtkjOSh0F.KflsOx8eolg1MTZdqUXcfzZQ4piogm8.PosK', '김강사', '1988-03-12', '010-1111-1111', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS001'),
    ('instructor02', 'instructor2@test.com', '$2a$10$instructor', '이강사', '1990-07-20', '010-2222-2222', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS002'),
    ('student01', 'student1@test.com', '$2a$10$vYwWcwkU/lHpqmL3WERfgOi0yx/OxCkfbtGQHJjIj8IpfTdGptORi', '박학생', '2001-05-14', '010-3333-3333', 'STUDENT', 'ACTIVE', TRUE, 'STD001'),
    ('student02', 'student2@test.com', '$2a$10$student', '최학생', '2000-10-03', '010-4444-4444', 'STUDENT', 'ACTIVE', TRUE, 'STD002'),
    ('student03', 'student3@test.com', '$2a$10$student', '정학생', '1999-12-25', '010-5555-5555', 'STUDENT', 'ACTIVE', TRUE, 'STD003');

INSERT IGNORE INTO categories
(main_category_id, name, sub_category, sort_order, is_active)
VALUES
    (1, 'ai·데이터', 'ChatGPT 활용', 1, TRUE),
    (1, 'ai·데이터', '프롬프트 엔지니어링', 2, TRUE),
    (1, 'ai·데이터', 'AI 이미지 생성', 3, TRUE),
    (1, 'ai·데이터', '데이터 분석', 4, TRUE),
    (1, 'ai·데이터', 'Python 데이터 처리', 5, TRUE),
    (1, 'ai·데이터', '데이터 시각화', 6, TRUE),
    (2, '건강·자격증', '요가 지도사', 7, TRUE),
    (2, '건강·자격증', '퍼스널트레이너', 8, TRUE),
    (2, '건강·자격증', '필라테스 지도사', 9, TRUE),
    (2, '건강·자격증', '스포츠 마사지', 10, TRUE),
    (2, '건강·자격증', '영양사', 11, TRUE),
    (3, '라이프·교육', '요리', 12, TRUE),
    (3, '라이프·교육', '홈 인테리어', 13, TRUE),
    (3, '라이프·교육', '반려동물 케어', 14, TRUE),
    (3, '라이프·교육', '글쓰기', 15, TRUE),
    (3, '라이프·교육', '독서법', 16, TRUE),
    (3, '라이프·교육', '자기계발', 17, TRUE),
    (4, '마케팅·비즈니스', 'SNS 마케팅', 18, TRUE),
    (4, '마케팅·비즈니스', '콘텐츠 마케팅', 19, TRUE),
    (4, '마케팅·비즈니스', '유튜브 운영', 20, TRUE),
    (4, '마케팅·비즈니스', '퍼포먼스 마케팅', 21, TRUE),
    (4, '마케팅·비즈니스', '창업·스타트업', 22, TRUE),
    (4, '마케팅·비즈니스', '브랜드 마케팅', 23, TRUE),
    (5, '외국어', '영어', 24, TRUE),
    (5, '외국어', '일본어', 25, TRUE),
    (5, '외국어', '중국어', 26, TRUE),
    (5, '외국어', '스페인어', 27, TRUE),
    (5, '외국어', '영어 회화', 28, TRUE),
    (6, '재테크·투자', '주식', 29, TRUE),
    (6, '재테크·투자', '부동산', 30, TRUE),
    (6, '재테크·투자', 'ETF·펀드', 31, TRUE),
    (6, '재테크·투자', '암호화폐', 32, TRUE),
    (6, '재테크·투자', '절세·세금', 33, TRUE),
    (6, '재테크·투자', '경제 공부', 34, TRUE),
    (7, '취미·문화', '그림', 35, TRUE),
    (7, '취미·문화', '사진·영상', 36, TRUE),
    (7, '취미·문화', '음악·악기', 37, TRUE),
    (7, '취미·문화', '공예·DIY', 38, TRUE),
    (7, '취미·문화', '댄스', 39, TRUE);

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
