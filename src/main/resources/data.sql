INSERT INTO users
(login_id, email, password, name, birth_date, phone, role, status, email_verified, referral_code)
VALUES
    ('admin01', 'admin@test.com', '$2a$10$admin', '관리재', '1985-01-01', '010-0000-0000', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN001'),
    ('instructor01', 'instructor1@test.com', '$2a$10$instructor', '김강사', '1988-03-12', '010-1111-1111', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS001'),
    ('instructor02', 'instructor2@test.com', '$2a$10$instructor', '이강사', '1990-07-20', '010-2222-2222', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS002'),
    ('student01', 'student1@test.com', '$2a$10$vYwWcwkU/lHpqmL3WERfgOi0yx/OxCkfbtGQHJjIj8IpfTdGptORi', '박학생', '2001-05-14', '010-3333-3333', 'STUDENT', 'ACTIVE', TRUE, 'STD001'),
    ('student02', 'student2@test.com', '$2a$10$student', '최학생', '2000-10-03', '010-4444-4444', 'STUDENT', 'ACTIVE', TRUE, 'STD002'),
    ('student03', 'student3@test.com', '$2a$10$student', '정학생', '1999-12-25', '010-5555-5555', 'STUDENT', 'ACTIVE', TRUE, 'STD003');

INSERT INTO categories
(name, sub_category, sort_order, is_active)
VALUES
    -- AI·데이터
    ('ai·데이터', 'ChatGPT 활용', 1, TRUE),
    ('ai·데이터', '프롬프트 엔지니어링', 2, TRUE),
    ('ai·데이터', 'AI 이미지 생성', 3, TRUE),
    ('ai·데이터', '데이터 분석', 4, TRUE),
    ('ai·데이터', 'Python 데이터 처리', 5, TRUE),
    ('ai·데이터', '데이터 시각화', 6, TRUE),

    -- 건강·자격증
    ('건강·자격증', '요가 지도사', 7, TRUE),
    ('건강·자격증', '퍼스널트레이너', 8, TRUE),
    ('건강·자격증', '필라테스 지도사', 9, TRUE),
    ('건강·자격증', '스포츠 마사지', 10, TRUE),
    ('건강·자격증', '영양사', 11, TRUE),

    -- 라이프·교육
    ('라이프·교육', '요리', 12, TRUE),
    ('라이프·교육', '홈 인테리어', 13, TRUE),
    ('라이프·교육', '반려동물 케어', 14, TRUE),
    ('라이프·교육', '글쓰기', 15, TRUE),
    ('라이프·교육', '독서법', 16, TRUE),
    ('라이프·교육', '자기계발', 17, TRUE),

    -- 마케팅·비즈니스
    ('마케팅·비즈니스', 'SNS 마케팅', 18, TRUE),
    ('마케팅·비즈니스', '콘텐츠 마케팅', 19, TRUE),
    ('마케팅·비즈니스', '유튜브 운영', 20, TRUE),
    ('마케팅·비즈니스', '퍼포먼스 마케팅', 21, TRUE),
    ('마케팅·비즈니스', '창업·스타트업', 22, TRUE),
    ('마케팅·비즈니스', '브랜드 마케팅', 23, TRUE),

    -- 외국어
    ('외국어', '영어', 24, TRUE),
    ('외국어', '일본어', 25, TRUE),
    ('외국어', '중국어', 26, TRUE),
    ('외국어', '스페인어', 27, TRUE),
    ('외국어', '영어 회화', 28, TRUE),

    -- 재테크·투자
    ('재테크·투자', '주식', 29, TRUE),
    ('재테크·투자', '부동산', 30, TRUE),
    ('재테크·투자', 'ETF·펀드', 31, TRUE),
    ('재테크·투자', '암호화폐', 32, TRUE),
    ('재테크·투자', '절세·세금', 33, TRUE),
    ('재테크·투자', '경제 공부', 34, TRUE),

    -- 취미·문화
    ('취미·문화', '그림', 35, TRUE),
    ('취미·문화', '사진·영상', 36, TRUE),
    ('취미·문화', '음악·악기', 37, TRUE),
    ('취미·문화', '공예·DIY', 38, TRUE),
    ('취미·문화', '댄스', 39, TRUE);

INSERT INTO courses
(title, description, price, difficulty, thumbnail, total_duration, status, reject_reason, rating_avg, review_count, student_count, created_at, approved_at, category_id, instructor_id)
VALUES
    -- AI·데이터 (category_id: 1~6)
    ('ChatGPT로 업무 자동화 완성하기', 'ChatGPT를 활용해 반복 업무를 자동화하고 생산성을 10배 높이는 실전 강의입니다.', 49000, 'BEGINNER', 'thumbnails/ai_data.jpg', 320, 'APPROVED', NULL, 4.85, 230, 1200, '2025-01-10 09:00:00', '2025-01-12 10:00:00', 1, 2),
    ('프롬프트 엔지니어링 마스터 클래스', 'AI 모델에서 원하는 결과를 뽑아내는 프롬프트 설계 전략을 체계적으로 배웁니다.', 59000, 'INTERMEDIATE', 'thumbnails/ai_data.jpg', 480, 'APPROVED', NULL, 4.90, 185, 980, '2025-01-15 09:00:00', '2025-01-17 10:00:00', 2, 3),
    ('Midjourney로 AI 이미지 생성 입문', 'Midjourney와 Stable Diffusion을 활용한 AI 이미지 생성 기초부터 실전까지 배웁니다.', 39000, 'BEGINNER', 'thumbnails/ai_data.jpg', 260, 'APPROVED', NULL, 4.70, 142, 870, '2025-02-01 09:00:00', '2025-02-03 10:00:00', 3, 2),
    ('Python으로 시작하는 데이터 분석', 'Pandas, NumPy를 활용해 데이터를 수집·정제·분석하는 실무 중심 강의입니다.', 69000, 'INTERMEDIATE', 'thumbnails/ai_data.jpg', 600, 'APPROVED', NULL, 4.80, 310, 1500, '2025-02-10 09:00:00', '2025-02-12 10:00:00', 4, 3),
    ('SQL 기초부터 실전까지', '데이터베이스 설계와 SQL 쿼리 작성법을 실습 위주로 학습합니다.', 45000, 'BEGINNER', 'thumbnails/ai_data.jpg', 400, 'APPROVED', NULL, 4.75, 198, 1100, '2025-02-20 09:00:00', '2025-02-22 10:00:00', 5, 2),
    ('Tableau로 데이터 시각화 완성', '비개발자도 쉽게 배우는 Tableau 대시보드 제작 실전 강의입니다.', 55000, 'INTERMEDIATE', 'thumbnails/ai_data.jpg', 380, 'APPROVED', NULL, 4.65, 120, 650, '2025-03-01 09:00:00', '2025-03-03 10:00:00', 6, 3),

    -- 건강·자격증 (category_id: 7~11)
    ('요가 지도사 2급 자격증 완벽 대비', '요가 지도사 2급 자격증 취득을 위한 이론과 실기를 체계적으로 준비합니다.', 89000, 'BEGINNER', 'thumbnails/health.jpg', 720, 'APPROVED', NULL, 4.88, 275, 980, '2025-01-20 09:00:00', '2025-01-22 10:00:00', 7, 2),
    ('퍼스널트레이너 자격증 단기 완성', 'NSCA, CPT 등 퍼스널트레이너 자격증 취득을 위한 핵심 이론과 실기를 다룹니다.', 99000, 'INTERMEDIATE', 'thumbnails/health.jpg', 800, 'APPROVED', NULL, 4.92, 320, 1400, '2025-02-05 09:00:00', '2025-02-07 10:00:00', 8, 3),
    ('필라테스 지도사 입문 과정', '필라테스 기초 동작부터 지도법까지 체계적으로 배우는 자격증 준비 강의입니다.', 79000, 'BEGINNER', 'thumbnails/health.jpg', 640, 'APPROVED', NULL, 4.78, 195, 820, '2025-02-15 09:00:00', '2025-02-17 10:00:00', 9, 2),
    ('스포츠 마사지 실전 테크닉', '근육 해부학 기반 스포츠 마사지 기법을 단계적으로 습득합니다.', 75000, 'INTERMEDIATE', 'thumbnails/health.jpg', 560, 'APPROVED', NULL, 4.70, 148, 620, '2025-03-05 09:00:00', '2025-03-07 10:00:00', 10, 3),
    ('영양사 국가시험 핵심 요약', '영양사 국가시험을 위한 핵심 이론과 기출문제 분석 강의입니다.', 85000, 'ADVANCED', 'thumbnails/health.jpg', 900, 'APPROVED', NULL, 4.82, 210, 750, '2025-03-10 09:00:00', '2025-03-12 10:00:00', 11, 2),

    -- 라이프·교육 (category_id: 12~17)
    ('집에서 배우는 이탈리안 요리', '파스타, 피자 등 이탈리안 요리를 집에서 쉽게 만드는 레시피 강의입니다.', 35000, 'BEGINNER', 'thumbnails/life.jpg', 300, 'APPROVED', NULL, 4.80, 260, 1300, '2025-01-25 09:00:00', '2025-01-27 10:00:00', 12, 3),
    ('홈 인테리어 셀프 리모델링', '예산 내에서 집을 감각적으로 꾸미는 인테리어 노하우를 공유합니다.', 42000, 'BEGINNER', 'thumbnails/life.jpg', 340, 'APPROVED', NULL, 4.72, 180, 890, '2025-02-08 09:00:00', '2025-02-10 10:00:00', 13, 2),
    ('반려동물 건강 관리 완벽 가이드', '강아지·고양이의 건강 관리, 식이요법, 응급처치를 배웁니다.', 38000, 'BEGINNER', 'thumbnails/life.jpg', 280, 'APPROVED', NULL, 4.85, 220, 1050, '2025-02-18 09:00:00', '2025-02-20 10:00:00', 14, 3),
    ('하루 30분 글쓰기 습관 만들기', '매일 꾸준히 글을 쓰는 습관을 형성하고 표현력을 키우는 강의입니다.', 29000, 'BEGINNER', 'thumbnails/life.jpg', 240, 'APPROVED', NULL, 4.68, 155, 780, '2025-03-03 09:00:00', '2025-03-05 10:00:00', 15, 2),
    ('독서 속도 3배 올리는 속독법', '효율적인 독서 방법과 기억력을 높이는 속독 기술을 배웁니다.', 32000, 'BEGINNER', 'thumbnails/life.jpg', 260, 'APPROVED', NULL, 4.60, 130, 670, '2025-03-15 09:00:00', '2025-03-17 10:00:00', 16, 3),
    ('자기계발 로드맵 설계하기', '목표 설정부터 실행까지 나만의 자기계발 플랜을 만드는 강의입니다.', 33000, 'BEGINNER', 'thumbnails/life.jpg', 270, 'APPROVED', NULL, 4.73, 175, 840, '2025-03-20 09:00:00', '2025-03-22 10:00:00', 17, 2),

    -- 마케팅·비즈니스 (category_id: 18~23)
    ('인스타그램 팔로워 10만 만들기', '인스타그램 알고리즘 분석과 콘텐츠 전략으로 팔로워를 빠르게 늘리는 방법을 배웁니다.', 55000, 'BEGINNER', 'thumbnails/marketing.jpg', 420, 'APPROVED', NULL, 4.88, 340, 1800, '2025-01-18 09:00:00', '2025-01-20 10:00:00', 18, 3),
    ('바이럴 콘텐츠 기획 전략', '공유되는 콘텐츠의 법칙을 분석하고 기획하는 마케팅 전략을 배웁니다.', 62000, 'INTERMEDIATE', 'thumbnails/marketing.jpg', 460, 'APPROVED', NULL, 4.82, 265, 1200, '2025-02-03 09:00:00', '2025-02-05 10:00:00', 19, 2),
    ('유튜브 채널 0에서 1만 구독자까지', '유튜브 채널 개설부터 수익화까지 전 과정을 실전으로 배웁니다.', 72000, 'BEGINNER', 'thumbnails/marketing.jpg', 580, 'APPROVED', NULL, 4.90, 410, 2100, '2025-02-12 09:00:00', '2025-02-14 10:00:00', 20, 3),
    ('구글 애즈 퍼포먼스 마케팅 실전', '구글 광고 세팅부터 성과 분석까지 ROI를 높이는 실무 전략을 배웁니다.', 79000, 'INTERMEDIATE', 'thumbnails/marketing.jpg', 520, 'APPROVED', NULL, 4.75, 195, 920, '2025-02-22 09:00:00', '2025-02-24 10:00:00', 21, 2),
    ('1인 창업 완벽 로드맵', '아이디어 발굴부터 사업 등록, 첫 매출까지 1인 창업의 모든 것을 다룹니다.', 89000, 'INTERMEDIATE', 'thumbnails/marketing.jpg', 680, 'APPROVED', NULL, 4.85, 280, 1350, '2025-03-08 09:00:00', '2025-03-10 10:00:00', 22, 3),
    ('브랜드 아이덴티티 구축 전략', '작은 브랜드도 강하게 만드는 브랜딩 전략과 실행 방법을 배웁니다.', 65000, 'INTERMEDIATE', 'thumbnails/marketing.jpg', 440, 'APPROVED', NULL, 4.78, 170, 810, '2025-03-18 09:00:00', '2025-03-20 10:00:00', 23, 2),

    -- 외국어 (category_id: 24~28)
    ('왕초보 영어 말하기 30일 완성', '영어를 한 번도 못 했던 분들을 위한 기초 회화 집중 강의입니다.', 45000, 'BEGINNER', 'thumbnails/language.jpg', 360, 'APPROVED', NULL, 4.87, 380, 2000, '2025-01-12 09:00:00', '2025-01-14 10:00:00', 24, 3),
    ('JLPT N3 단기 합격 전략', '일본어 능력시험 N3를 단기간에 합격하는 핵심 문법과 어휘를 학습합니다.', 55000, 'INTERMEDIATE', 'thumbnails/language.jpg', 480, 'APPROVED', NULL, 4.80, 220, 1050, '2025-01-22 09:00:00', '2025-01-24 10:00:00', 25, 2),
    ('비즈니스 중국어 실전 회화', '중국 거래처와 바로 소통할 수 있는 비즈니스 중국어 회화를 배웁니다.', 59000, 'INTERMEDIATE', 'thumbnails/language.jpg', 420, 'APPROVED', NULL, 4.72, 165, 780, '2025-02-02 09:00:00', '2025-02-04 10:00:00', 26, 3),
    ('스페인어 입문 여행 회화', '스페인·중남미 여행에서 바로 쓰는 스페인어 기초 회화를 배웁니다.', 38000, 'BEGINNER', 'thumbnails/language.jpg', 300, 'APPROVED', NULL, 4.68, 140, 690, '2025-02-14 09:00:00', '2025-02-16 10:00:00', 27, 2),
    ('원어민처럼 말하는 영어 회화', '네이티브 표현과 뉘앙스를 익혀 자연스러운 영어 회화를 완성합니다.', 62000, 'ADVANCED', 'thumbnails/language.jpg', 500, 'APPROVED', NULL, 4.92, 295, 1450, '2025-02-24 09:00:00', '2025-02-26 10:00:00', 28, 3),

    -- 재테크·투자 (category_id: 29~34)
    ('주식 투자 기초 완전 정복', '주식 시장의 원리부터 종목 선택, 매매 타이밍까지 기초를 탄탄히 잡습니다.', 59000, 'BEGINNER', 'thumbnails/finance.jpg', 480, 'APPROVED', NULL, 4.88, 420, 2200, '2025-01-08 09:00:00', '2025-01-10 10:00:00', 29, 2),
    ('부동산 소액 투자 입문', '적은 돈으로 시작하는 부동산 투자 전략과 리스크 관리를 배웁니다.', 79000, 'INTERMEDIATE', 'thumbnails/finance.jpg', 560, 'APPROVED', NULL, 4.82, 310, 1600, '2025-01-28 09:00:00', '2025-01-30 10:00:00', 30, 3),
    ('ETF로 시작하는 장기 투자', 'ETF의 원리와 포트폴리오 구성으로 안정적인 장기 투자 전략을 세웁니다.', 49000, 'BEGINNER', 'thumbnails/finance.jpg', 380, 'APPROVED', NULL, 4.78, 255, 1350, '2025-02-06 09:00:00', '2025-02-08 10:00:00', 31, 2),
    ('비트코인·알트코인 투자 전략', '암호화폐 시장 분석과 리스크 관리 기반의 투자 전략을 배웁니다.', 65000, 'INTERMEDIATE', 'thumbnails/finance.jpg', 460, 'APPROVED', NULL, 4.65, 185, 950, '2025-02-16 09:00:00', '2025-02-18 10:00:00', 32, 3),
    ('직장인을 위한 절세 전략', '연말정산, 종합소득세 절세 방법과 세금 줄이는 합법적인 방법을 배웁니다.', 45000, 'BEGINNER', 'thumbnails/finance.jpg', 340, 'APPROVED', NULL, 4.85, 298, 1480, '2025-02-26 09:00:00', '2025-02-28 10:00:00', 33, 2),
    ('경제 뉴스 제대로 읽는 법', '거시경제 지표와 뉴스를 투자에 연결하는 경제 독해력을 키웁니다.', 35000, 'BEGINNER', 'thumbnails/finance.jpg', 300, 'APPROVED', NULL, 4.70, 215, 1100, '2025-03-06 09:00:00', '2025-03-08 10:00:00', 34, 3),

    -- 취미·문화 (category_id: 35~39)
    ('디지털 드로잉 입문 with 아이패드', '아이패드 Procreate로 캐릭터와 일러스트를 그리는 기초 강의입니다.', 42000, 'BEGINNER', 'thumbnails/hobby.jpg', 360, 'APPROVED', NULL, 4.90, 350, 1900, '2025-01-15 09:00:00', '2025-01-17 10:00:00', 35, 2),
    ('스마트폰으로 영화 같은 영상 만들기', '스마트폰 촬영 기법과 편집 앱으로 고퀄리티 영상을 제작합니다.', 48000, 'BEGINNER', 'thumbnails/hobby.jpg', 400, 'APPROVED', NULL, 4.82, 275, 1300, '2025-01-25 09:00:00', '2025-01-27 10:00:00', 36, 3),
    ('통기타 왕초보 30일 완성', '코드 잡는 법부터 자작곡 연주까지 통기타를 처음 배우는 분을 위한 강의입니다.', 39000, 'BEGINNER', 'thumbnails/hobby.jpg', 320, 'APPROVED', NULL, 4.85, 305, 1600, '2025-02-04 09:00:00', '2025-02-06 10:00:00', 37, 2),
    ('손뜨개 소품 만들기 기초', '코바늘과 대바늘로 귀여운 소품을 만드는 뜨개질 기초 강의입니다.', 33000, 'BEGINNER', 'thumbnails/hobby.jpg', 280, 'APPROVED', NULL, 4.75, 190, 920, '2025-02-14 09:00:00', '2025-02-16 10:00:00', 38, 3),
    ('K-POP 댄스 기초 클래스', 'K-POP 안무를 배우며 춤의 기초 체력과 리듬감을 키우는 댄스 강의입니다.', 36000, 'BEGINNER', 'thumbnails/hobby.jpg', 300, 'APPROVED', NULL, 4.88, 330, 1750, '2025-02-24 09:00:00', '2025-02-26 10:00:00', 39, 2),

    -- 심사중 / 반려 / 임시저장 상태 샘플
    ('GPT-4o 활용 업무 자동화 심화', 'GPT-4o API를 연동해 실제 업무 자동화 시스템을 구축하는 심화 강의입니다.', 89000, 'ADVANCED', 'thumbnails/ai_data.jpg', 720, 'PENDING', NULL, 0.00, 0, 0, '2025-04-01 09:00:00', NULL, 1, 3),
    ('부동산 경매 실전 투자', '법원 경매 물건 분석부터 낙찰 후 처리까지 실전 경매 투자를 배웁니다.', 95000, 'ADVANCED', 'thumbnails/finance.jpg', 800, 'REJECTED', '강의 내용 중 법적 검토가 필요한 부분이 있어 반려합니다.', 0.00, 0, 0, '2025-04-05 09:00:00', NULL, 30, 2),
    ('수채화 풍경 그리기', '수채화 물감과 붓 사용법부터 풍경 완성까지 단계별로 배웁니다.', 41000, 'BEGINNER', 'thumbnails/hobby.jpg', 350, 'DRAFT', NULL, 0.00, 0, 0, '2025-04-10 09:00:00', NULL, 35, 3);

INSERT INTO course_sessions
(session_uid, title, video_url, duration_seconds, session_order, is_preview, course_id)
VALUES
    (UUID(), '시험 안내와 과목 구조', '/videos/cert/ip-written-01.mp4', 1200, 1, TRUE, 1),
    (UUID(), '소프트웨어 설계 핵심', '/videos/cert/ip-written-02.mp4', 1800, 2, FALSE, 1),
    (UUID(), '데이터베이스 구축 핵심', '/videos/cert/ip-written-03.mp4', 2400, 3, FALSE, 1),

    (UUID(), '실기 시험 출제 방식', '/videos/cert/ip-practical-01.mp4', 1500, 1, TRUE, 2),
    (UUID(), '요구사항 확인과 SQL 풀이', '/videos/cert/ip-practical-02.mp4', 2700, 2, FALSE, 2),

    (UUID(), '데이터 모델링 이해', '/videos/cert/sqld-01.mp4', 1600, 1, TRUE, 3),
    (UUID(), 'SQL 기본과 활용', '/videos/cert/sqld-02.mp4', 2200, 2, FALSE, 3),

    (UUID(), '빅데이터 분석 기획', '/videos/cert/bigdata-01.mp4', 1800, 1, TRUE, 4),
    (UUID(), '통계 기반 데이터 시각화', '/videos/cert/bigdata-02.mp4', 2100, 2, FALSE, 4);

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
    ('정보처리기사 필기 핵심 이론', 59000, 5000, 54000, 1, 1),
    ('정보처리기사 실기 문제풀이', 79000, 5000, 74000, 1, 2),
    ('SQLD 2주 완성', 69000, 0, 69000, 2, 3),
    ('정보처리기사 필기 핵심 이론', 59000, 0, 59000, 3, 1);

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
    (5, '정보처리기사 필기 입문자가 듣기 좋았습니다.', 'ACTIVE', 4, 1),
    (4, '설명이 친절하고 기출 포인트가 많아서 좋았습니다.', 'ACTIVE', 5, 1),
    (5, 'SQLD 기본 개념을 잡기에 좋았습니다.', 'ACTIVE', 5, 3);

INSERT INTO review_replies
(content, review_id, user_id)
VALUES
    ('좋은 리뷰 감사합니다. 다음 강의도 열심히 준비하겠습니다.', 1, 2),
    ('수강해주셔서 감사합니다!', 3, 3);

INSERT INTO course_qna
(title, content, status, course_id, session_id, user_id)
VALUES
    ('필기 3과목 공부 순서가 궁금합니다.', '데이터베이스 구축 과목은 어떤 순서로 공부하면 좋을까요?', 'WAITING', 1, 3, 4),
    ('실기 SQL 작성 질문입니다.', 'SQL 서술형 답안을 작성할 때 감점 포인트가 궁금합니다.', 'ANSWERED', 2, 5, 4);

INSERT INTO course_announcements
(title, content, is_pinned, course_id, user_id)
VALUES
    ('정보처리기사 필기 자료 안내', '섹션별 요약 노트와 기출 체크리스트는 첨부파일에서 확인 가능합니다.', TRUE, 1, 2),
    ('SQLD 실습 환경 안내', 'SQL 실습 파일과 예제 데이터는 첨부파일에서 확인해주세요.', TRUE, 3, 3);

INSERT INTO coupons
(code, name, discount_type, discount_value, min_order_amount, started_at, expired_at, is_active)
VALUES
    ('WELCOME10000', '신규 가입 1만원 할인', 'AMOUNT', 10000, 50000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE),
    ('SPRING20', '봄맞이 20% 할인', 'PERCENT', 20, 30000, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), TRUE),
    ('CERT5000', '자격증 강의 5천원 할인', 'AMOUNT', 5000, 30000, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE);

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
(bio, portfolio_url, certification_name, issued_by, approval_status, approved_at, user_id)
VALUES
    ('정보처리기사와 SQLD 자격증 강의를 전문으로 합니다.', 'https://portfolio.example.com/instructor1', '정보처리기사', '한국산업인력공단', 'APPROVED', NOW(), 2),
    ('데이터 분석 자격증과 빅데이터분석기사 강의를 진행합니다.', 'https://portfolio.example.com/instructor2', 'ADsP', '한국데이터산업진흥원', 'APPROVED', NOW(), 3);

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
    ('PAYMENT', '결제가 완료되었습니다.', '정보처리기사 필기 핵심 이론 외 1개 강의 결제가 완료되었습니다.', '/orders/1', FALSE, 4),
    ('COURSE', '수강이 시작되었습니다.', '정보처리기사 필기 핵심 이론 강의를 바로 수강할 수 있습니다.', '/courses/1', FALSE, 4),
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
    ('이력서 생성 프롬프트 v1', 'RESUME_GENERATION',
     '사용자의 경력과 기술스택을 바탕으로 신입 개발자 이력서 초안을 작성해주세요. 사용자 정보: {userProfile}',
     1, TRUE),

    ('이력서 평가 프롬프트 v1', 'RESUME_EVALUATION',
     '아래 이력서 내용을 기반으로 강점, 약점, 개선점을 평가해주세요. 이력서 제목: {resumeTitle}, 이력서 내용: {resumeContent}',
     1, TRUE),

    ('채용공고 분석 프롬프트 v1', 'JOB_ANALYSIS',
     '아래 채용공고 내용을 분석해서 직무명, 요구 역량, 추천 자격증, 보완 학습이 필요한 강의 방향을 도출해주세요. 이력서 보유 여부: {resumeBased}, 채용공고 내용: {jobPostingContent}',
     1, TRUE),

    ('강의 추천 프롬프트 v1', 'COURSE_RECOMMENDATION',
     '사용자의 목표, 부족한 기술, 채용공고 요구 역량을 바탕으로 적합한 강의를 추천해주세요. 목표: {targetGoal}, 부족한 기술: {missingSkills}, 요구 역량: {requiredSkills}',
     1, TRUE);

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
    ('BASIC', 4, 1),
    ('BASIC', 1, 2),
    ('INTERMEDIATE', 3, 2),
    ('INTERMEDIATE', 4, 2),
    ('BASIC', 4, 3),
    ('BASIC', 7, 4),
    ('BASIC', 8, 4),
    ('BASIC', 9, 4);

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
        '정보처리기사 실기 대비 SQL과 문제풀이 역량 보완이 필요하여 실기 문제풀이 강의를 추천합니다.',
        JSON_OBJECT(
                'reasonType', 'SKILL_GAP',
                'missingSkill', 'SQL 서술형 문제풀이',
                'recommendedCourse', '정보처리기사 실기 문제풀이'
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
        '데이터 자격증 준비를 위해 SQL 기본기와 데이터 모델링 역량을 보완할 수 있는 SQLD 강의를 추천합니다.',
        JSON_OBJECT(
                'reasonType', 'CERTIFICATION_MATCH',
                'targetSkill', 'SQL',
                'recommendedCourse', 'SQLD 2주 완성'
        ),
        4,
        5
    );

INSERT INTO certifications
(name, description, level, issuing_organization, pass_rate, employment_rate, external_url, category_id)
VALUES
    ('정보처리기사', '소프트웨어 개발 및 정보시스템 구축 역량을 평가하는 국가기술자격입니다.', '기사', '한국산업인력공단', 55.20, 68.50, 'https://www.q-net.or.kr', 1),
    ('SQLD', 'SQL 활용 능력과 데이터 모델링 이해도를 평가하는 국가공인 민간자격입니다.', '국가공인 민간자격', '한국데이터산업진흥원', 45.30, 61.00, 'https://www.dataq.or.kr', 4),
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
    ('JOB', '데이터 자격증 기반 취업 준비 로드맵', '데이터 분석가', '2026-09-30', TRUE, 'ACTIVE', 4),
    ('COURSE', 'SQLD 2주 완성 로드맵', 'SQLD 2주 완성', '2026-06-30', FALSE, 'ACTIVE', 5);

INSERT INTO roadmap_steps
(title, description, step_order, status, course_id, roadmap_id, certification_id)
VALUES
    ('자격증 시험 정보 확인', '시험 일정, 응시 조건, 과목을 확인합니다.', 1, 'DONE', NULL, 1, 1),
    ('기초 이론 학습', '소프트웨어 공학, 데이터베이스, 운영체제 기초를 학습합니다.', 2, 'IN_PROGRESS', NULL, 1, 1),
    ('기출문제 풀이', '최근 5개년 기출문제를 반복 풀이합니다.', 3, 'TODO', NULL, 1, 1),

    ('정보처리기사 필기 강의 수강', '자격증 기본기를 위해 정보처리기사 필기 핵심 이론 강의를 수강합니다.', 1, 'DONE', 1, 2, NULL),
    ('SQLD 기본기 보완', 'SQLD 2주 완성 강의를 통해 SQL과 데이터 모델링을 학습합니다.', 2, 'IN_PROGRESS', 3, 2, NULL),
    ('이력서 자격증 섹션 개선', 'AI 평가 결과를 바탕으로 자격증 학습 이력과 프로젝트 경험을 보완합니다.', 3, 'TODO', NULL, 2, NULL),

    ('SQLD 기본 학습', '데이터 모델링, SQL 기본, SQL 활용을 학습합니다.', 1, 'IN_PROGRESS', 3, 3, NULL),
    ('기출 유형 풀이', 'SQLD 빈출 유형과 오답 포인트를 반복 학습합니다.', 2, 'TODO', 3, 3, NULL);

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
    (520, '데이터베이스 구축 핵심 다시 보기', 4, 3),
    (900, '실기 SQL 감점 포인트 중요', 4, 5),
    (300, 'SQLD 데이터 모델링 개념 복습', 5, 6);

INSERT INTO lecture_notes
(title, content, session_id, user_id)
VALUES
    ('데이터베이스 구축 정리', '데이터베이스 설계와 SQL 기본 개념은 필기와 실기 모두에서 자주 출제된다.', 3, 4),
    ('실기 SQL 정리', 'SQL 답안은 조건 누락과 컬럼명 오류를 특히 주의해야 한다.', 5, 4),
    ('SQLD 모델링 정리', '엔터티, 속성, 관계 개념을 구분해서 암기한다.', 7, 5);

INSERT INTO reports
(target_type, target_id, reason, status, processed_at, user_id)
VALUES
    ('REVIEW', 2, '부적절한 표현이 포함되어 있습니다.', 'PENDING', NULL, 6),
    ('QNA', 1, '질문 내용이 강의와 무관합니다.', 'REJECTED', NOW(), 2);

INSERT INTO admin_logs
(action_type, target_type, target_id, description, user_id)
VALUES
    ('APPROVE_COURSE', 'COURSE', 1, '정보처리기사 필기 핵심 이론 강의를 승인했습니다.', 1),
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
    ('데이터 분석가 지원 마감', '2026-09-30', 'JOB', 4),
    ('SQLD 강의 완강 목표', '2026-06-30', 'COURSE', 5);
