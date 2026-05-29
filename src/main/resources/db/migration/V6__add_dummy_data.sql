INSERT IGNORE INTO courses
(title, description, price, difficulty, thumbnail, total_duration, status, reject_reason, rating_avg, review_count, student_count, created_at, approved_at, category_id, instructor_id)
VALUES
    ('ChatGPT로 업무 자동화 완성하기', 'ChatGPT를 활용해 반복 업무를 자동화하고 생산성을 10배 높이는 실전 강의입니다.', 49000, 'BEGINNER', 'thumbnails/ai_data.jpg', 320, 'APPROVED', NULL, 4.85, 230, 1200, '2025-01-10 09:00:00', '2025-01-12 10:00:00', 1, 2),
    ('프롬프트 엔지니어링 마스터 클래스', 'AI 모델에서 원하는 결과를 뽑아내는 프롬프트 설계 전략을 체계적으로 배웁니다.', 59000, 'INTERMEDIATE', 'thumbnails/ai_data.jpg', 480, 'APPROVED', NULL, 4.90, 185, 980, '2025-01-15 09:00:00', '2025-01-17 10:00:00', 2, 3),
    ('Midjourney로 AI 이미지 생성 입문', 'Midjourney와 Stable Diffusion을 활용한 AI 이미지 생성 기초부터 실전까지 배웁니다.', 39000, 'BEGINNER', 'thumbnails/ai_data.jpg', 260, 'APPROVED', NULL, 4.70, 142, 870, '2025-02-01 09:00:00', '2025-02-03 10:00:00', 3, 2),
    ('Python으로 시작하는 데이터 분석', 'Pandas, NumPy를 활용해 데이터를 수집·정제·분석하는 실무 중심 강의입니다.', 69000, 'INTERMEDIATE', 'thumbnails/ai_data.jpg', 600, 'APPROVED', NULL, 4.80, 310, 1500, '2025-02-10 09:00:00', '2025-02-12 10:00:00', 4, 3),
    ('SQL 기초부터 실전까지', '데이터베이스 설계와 SQL 쿼리 작성법을 실습 위주로 학습합니다.', 45000, 'BEGINNER', 'thumbnails/ai_data.jpg', 400, 'APPROVED', NULL, 4.75, 198, 1100, '2025-02-20 09:00:00', '2025-02-22 10:00:00', 5, 2),
    ('Tableau로 데이터 시각화 완성', '비개발자도 쉽게 배우는 Tableau 대시보드 제작 실전 강의입니다.', 55000, 'INTERMEDIATE', 'thumbnails/ai_data.jpg', 380, 'APPROVED', NULL, 4.65, 120, 650, '2025-03-01 09:00:00', '2025-03-03 10:00:00', 6, 3),
    ('요가 지도사 2급 자격증 완벽 대비', '요가 지도사 2급 자격증 취득을 위한 이론과 실기를 체계적으로 준비합니다.', 89000, 'BEGINNER', 'thumbnails/health.jpg', 720, 'APPROVED', NULL, 4.88, 275, 980, '2025-01-20 09:00:00', '2025-01-22 10:00:00', 7, 2),
    ('퍼스널트레이너 자격증 단기 완성', 'NSCA, CPT 등 퍼스널트레이너 자격증 취득을 위한 핵심 이론과 실기를 다룹니다.', 99000, 'INTERMEDIATE', 'thumbnails/health.jpg', 800, 'APPROVED', NULL, 4.92, 320, 1400, '2025-02-05 09:00:00', '2025-02-07 10:00:00', 8, 3),
    ('필라테스 지도사 입문 과정', '필라테스 기초 동작부터 지도법까지 체계적으로 배우는 자격증 준비 강의입니다.', 79000, 'BEGINNER', 'thumbnails/health.jpg', 640, 'APPROVED', NULL, 4.78, 195, 820, '2025-02-15 09:00:00', '2025-02-17 10:00:00', 9, 2),
    ('스포츠 마사지 실전 테크닉', '근육 해부학 기반 스포츠 마사지 기법을 단계적으로 습득합니다.', 75000, 'INTERMEDIATE', 'thumbnails/health.jpg', 560, 'APPROVED', NULL, 4.70, 148, 620, '2025-03-05 09:00:00', '2025-03-07 10:00:00', 10, 3),
    ('영양사 국가시험 핵심 요약', '영양사 국가시험을 위한 핵심 이론과 기출문제 분석 강의입니다.', 85000, 'ADVANCED', 'thumbnails/health.jpg', 900, 'APPROVED', NULL, 4.82, 210, 750, '2025-03-10 09:00:00', '2025-03-12 10:00:00', 11, 2),
    ('집에서 배우는 이탈리안 요리', '파스타, 피자 등 이탈리안 요리를 집에서 쉽게 만드는 레시피 강의입니다.', 35000, 'BEGINNER', 'thumbnails/life.jpg', 300, 'APPROVED', NULL, 4.80, 260, 1300, '2025-01-25 09:00:00', '2025-01-27 10:00:00', 12, 3),
    ('홈 인테리어 셀프 리모델링', '예산 내에서 집을 감각적으로 꾸미는 인테리어 노하우를 공유합니다.', 42000, 'BEGINNER', 'thumbnails/life.jpg', 340, 'APPROVED', NULL, 4.72, 180, 890, '2025-02-08 09:00:00', '2025-02-10 10:00:00', 13, 2),
    ('반려동물 건강 관리 완벽 가이드', '강아지·고양이의 건강 관리, 식이요법, 응급처치를 배웁니다.', 38000, 'BEGINNER', 'thumbnails/life.jpg', 280, 'APPROVED', NULL, 4.85, 220, 1050, '2025-02-18 09:00:00', '2025-02-20 10:00:00', 14, 3),
    ('하루 30분 글쓰기 습관 만들기', '매일 꾸준히 글을 쓰는 습관을 형성하고 표현력을 키우는 강의입니다.', 29000, 'BEGINNER', 'thumbnails/life.jpg', 240, 'APPROVED', NULL, 4.68, 155, 780, '2025-03-03 09:00:00', '2025-03-05 10:00:00', 15, 2),
    ('독서 속도 3배 올리는 속독법', '효율적인 독서 방법과 기억력을 높이는 속독 기술을 배웁니다.', 32000, 'BEGINNER', 'thumbnails/life.jpg', 260, 'APPROVED', NULL, 4.60, 130, 670, '2025-03-15 09:00:00', '2025-03-17 10:00:00', 16, 3),
    ('자기계발 로드맵 설계하기', '목표 설정부터 실행까지 나만의 자기계발 플랜을 만드는 강의입니다.', 33000, 'BEGINNER', 'thumbnails/life.jpg', 270, 'APPROVED', NULL, 4.73, 175, 840, '2025-03-20 09:00:00', '2025-03-22 10:00:00', 17, 2),
    ('인스타그램 팔로워 10만 만들기', '인스타그램 알고리즘 분석과 콘텐츠 전략으로 팔로워를 빠르게 늘리는 방법을 배웁니다.', 55000, 'BEGINNER', 'thumbnails/marketing.jpg', 420, 'APPROVED', NULL, 4.88, 340, 1800, '2025-01-18 09:00:00', '2025-01-20 10:00:00', 18, 3),
    ('바이럴 콘텐츠 기획 전략', '공유되는 콘텐츠의 법칙을 분석하고 기획하는 마케팅 전략을 배웁니다.', 62000, 'INTERMEDIATE', 'thumbnails/marketing.jpg', 460, 'APPROVED', NULL, 4.82, 265, 1200, '2025-02-03 09:00:00', '2025-02-05 10:00:00', 19, 2),
    ('유튜브 채널 0에서 1만 구독자까지', '유튜브 채널 개설부터 수익화까지 전 과정을 실전으로 배웁니다.', 72000, 'BEGINNER', 'thumbnails/marketing.jpg', 580, 'APPROVED', NULL, 4.90, 410, 2100, '2025-02-12 09:00:00', '2025-02-14 10:00:00', 20, 3),
    ('구글 애즈 퍼포먼스 마케팅 실전', '구글 광고 세팅부터 성과 분석까지 ROI를 높이는 실무 전략을 배웁니다.', 79000, 'INTERMEDIATE', 'thumbnails/marketing.jpg', 520, 'APPROVED', NULL, 4.75, 195, 920, '2025-02-22 09:00:00', '2025-02-24 10:00:00', 21, 2),
    ('1인 창업 완벽 로드맵', '아이디어 발굴부터 사업 등록, 첫 매출까지 1인 창업의 모든 것을 다룹니다.', 89000, 'INTERMEDIATE', 'thumbnails/marketing.jpg', 680, 'APPROVED', NULL, 4.85, 280, 1350, '2025-03-08 09:00:00', '2025-03-10 10:00:00', 22, 3),
    ('브랜드 아이덴티티 구축 전략', '작은 브랜드도 강하게 만드는 브랜딩 전략과 실행 방법을 배웁니다.', 65000, 'INTERMEDIATE', 'thumbnails/marketing.jpg', 440, 'APPROVED', NULL, 4.78, 170, 810, '2025-03-18 09:00:00', '2025-03-20 10:00:00', 23, 2),
    ('왕초보 영어 말하기 30일 완성', '영어를 한 번도 못 했던 분들을 위한 기초 회화 집중 강의입니다.', 45000, 'BEGINNER', 'thumbnails/language.jpg', 360, 'APPROVED', NULL, 4.87, 380, 2000, '2025-01-12 09:00:00', '2025-01-14 10:00:00', 24, 3),
    ('JLPT N3 단기 합격 전략', '일본어 능력시험 N3를 단기간에 합격하는 핵심 문법과 어휘를 학습합니다.', 55000, 'INTERMEDIATE', 'thumbnails/language.jpg', 480, 'APPROVED', NULL, 4.80, 220, 1050, '2025-01-22 09:00:00', '2025-01-24 10:00:00', 25, 2),
    ('비즈니스 중국어 실전 회화', '중국 거래처와 바로 소통할 수 있는 비즈니스 중국어 회화를 배웁니다.', 59000, 'INTERMEDIATE', 'thumbnails/language.jpg', 420, 'APPROVED', NULL, 4.72, 165, 780, '2025-02-02 09:00:00', '2025-02-04 10:00:00', 26, 3),
    ('스페인어 입문 여행 회화', '스페인·중남미 여행에서 바로 쓰는 스페인어 기초 회화를 배웁니다.', 38000, 'BEGINNER', 'thumbnails/language.jpg', 300, 'APPROVED', NULL, 4.68, 140, 690, '2025-02-14 09:00:00', '2025-02-16 10:00:00', 27, 2),
    ('원어민처럼 말하는 영어 회화', '네이티브 표현과 뉘앙스를 익혀 자연스러운 영어 회화를 완성합니다.', 62000, 'ADVANCED', 'thumbnails/language.jpg', 500, 'APPROVED', NULL, 4.92, 295, 1450, '2025-02-24 09:00:00', '2025-02-26 10:00:00', 28, 3),
    ('주식 투자 기초 완전 정복', '주식 시장의 원리부터 종목 선택, 매매 타이밍까지 기초를 탄탄히 잡습니다.', 59000, 'BEGINNER', 'thumbnails/finance.jpg', 480, 'APPROVED', NULL, 4.88, 420, 2200, '2025-01-08 09:00:00', '2025-01-10 10:00:00', 29, 2),
    ('부동산 소액 투자 입문', '적은 돈으로 시작하는 부동산 투자 전략과 리스크 관리를 배웁니다.', 79000, 'INTERMEDIATE', 'thumbnails/finance.jpg', 560, 'APPROVED', NULL, 4.82, 310, 1600, '2025-01-28 09:00:00', '2025-01-30 10:00:00', 30, 3),
    ('ETF로 시작하는 장기 투자', 'ETF의 원리와 포트폴리오 구성으로 안정적인 장기 투자 전략을 세웁니다.', 49000, 'BEGINNER', 'thumbnails/finance.jpg', 380, 'APPROVED', NULL, 4.78, 255, 1350, '2025-02-06 09:00:00', '2025-02-08 10:00:00', 31, 2),
    ('비트코인·알트코인 투자 전략', '암호화폐 시장 분석과 리스크 관리 기반의 투자 전략을 배웁니다.', 65000, 'INTERMEDIATE', 'thumbnails/finance.jpg', 460, 'APPROVED', NULL, 4.65, 185, 950, '2025-02-16 09:00:00', '2025-02-18 10:00:00', 32, 3),
    ('직장인을 위한 절세 전략', '연말정산, 종합소득세 절세 방법과 세금 줄이는 합법적인 방법을 배웁니다.', 45000, 'BEGINNER', 'thumbnails/finance.jpg', 340, 'APPROVED', NULL, 4.85, 298, 1480, '2025-02-26 09:00:00', '2025-02-28 10:00:00', 33, 2),
    ('경제 뉴스 제대로 읽는 법', '거시경제 지표와 뉴스를 투자에 연결하는 경제 독해력을 키웁니다.', 35000, 'BEGINNER', 'thumbnails/finance.jpg', 300, 'APPROVED', NULL, 4.70, 215, 1100, '2025-03-06 09:00:00', '2025-03-08 10:00:00', 34, 3),
    ('디지털 드로잉 입문 with 아이패드', '아이패드 Procreate로 캐릭터와 일러스트를 그리는 기초 강의입니다.', 42000, 'BEGINNER', 'thumbnails/hobby.jpg', 360, 'APPROVED', NULL, 4.90, 350, 1900, '2025-01-15 09:00:00', '2025-01-17 10:00:00', 35, 2),
    ('스마트폰으로 영화 같은 영상 만들기', '스마트폰 촬영 기법과 편집 앱으로 고퀄리티 영상을 제작합니다.', 48000, 'BEGINNER', 'thumbnails/hobby.jpg', 400, 'APPROVED', NULL, 4.82, 275, 1300, '2025-01-25 09:00:00', '2025-01-27 10:00:00', 36, 3),
    ('통기타 왕초보 30일 완성', '코드 잡는 법부터 자작곡 연주까지 통기타를 처음 배우는 분을 위한 강의입니다.', 39000, 'BEGINNER', 'thumbnails/hobby.jpg', 320, 'APPROVED', NULL, 4.85, 305, 1600, '2025-02-04 09:00:00', '2025-02-06 10:00:00', 37, 2),
    ('손뜨개 소품 만들기 기초', '코바늘과 대바늘로 귀여운 소품을 만드는 뜨개질 기초 강의입니다.', 33000, 'BEGINNER', 'thumbnails/hobby.jpg', 280, 'APPROVED', NULL, 4.75, 190, 920, '2025-02-14 09:00:00', '2025-02-16 10:00:00', 38, 3),
    ('K-POP 댄스 기초 클래스', 'K-POP 안무를 배우며 춤의 기초 체력과 리듬감을 키우는 댄스 강의입니다.', 36000, 'BEGINNER', 'thumbnails/hobby.jpg', 300, 'APPROVED', NULL, 4.88, 330, 1750, '2025-02-24 09:00:00', '2025-02-26 10:00:00', 39, 2),
    ('GPT-4o 활용 업무 자동화 심화', 'GPT-4o API를 연동해 실제 업무 자동화 시스템을 구축하는 심화 강의입니다.', 89000, 'ADVANCED', 'thumbnails/ai_data.jpg', 720, 'PENDING', NULL, 0.00, 0, 0, '2025-04-01 09:00:00', NULL, 1, 3),
    ('부동산 경매 실전 투자', '법원 경매 물건 분석부터 낙찰 후 처리까지 실전 경매 투자를 배웁니다.', 95000, 'ADVANCED', 'thumbnails/finance.jpg', 800, 'REJECTED', '강의 내용 중 법적 검토가 필요한 부분이 있어 반려합니다.', 0.00, 0, 0, '2025-04-05 09:00:00', NULL, 30, 2),
    ('수채화 풍경 그리기', '수채화 물감과 붓 사용법부터 풍경 완성까지 단계별로 배웁니다.', 41000, 'BEGINNER', 'thumbnails/hobby.jpg', 350, 'DRAFT', NULL, 0.00, 0, 0, '2025-04-10 09:00:00', NULL, 35, 3);

INSERT IGNORE INTO course_sessions
(session_uid, title, video_url, duration_seconds, session_order, is_preview, course_id)
VALUES
    (UUID(), '오리엔테이션 및 강의 안내', '/videos/course1/session1.mp4', 1200, 1, TRUE, 1),
    (UUID(), 'ChatGPT 기초 활용법', '/videos/course1/session2.mp4', 1800, 2, FALSE, 1),
    (UUID(), '업무 자동화 실습', '/videos/course1/session3.mp4', 2400, 3, FALSE, 1),
    (UUID(), '프롬프트 설계 기초', '/videos/course2/session1.mp4', 1500, 1, TRUE, 2),
    (UUID(), '고급 프롬프트 전략', '/videos/course2/session2.mp4', 2700, 2, FALSE, 2),
    (UUID(), 'Midjourney 기초', '/videos/course3/session1.mp4', 1600, 1, TRUE, 3),
    (UUID(), '이미지 생성 실습', '/videos/course3/session2.mp4', 2200, 2, FALSE, 3),
    (UUID(), 'Python 환경 설정', '/videos/course4/session1.mp4', 1800, 1, TRUE, 4),
    (UUID(), 'Pandas 기초', '/videos/course4/session2.mp4', 2100, 2, FALSE, 4),
    (UUID(), 'SQL 기초 문법', '/videos/course5/session1.mp4', 1800, 1, TRUE, 5),
    (UUID(), 'SQL 실습 프로젝트', '/videos/course5/session2.mp4', 2400, 2, FALSE, 5);

INSERT IGNORE INTO orders
(order_no, total_amount, discount_amount, final_amount, status, user_id)
VALUES
    ('ORD-20260521-0001', 108000, 10000, 98000, 'PAID', 4),
    ('ORD-20260521-0002', 59000, 0, 59000, 'PAID', 5),
    ('ORD-20260521-0003', 39000, 0, 39000, 'PENDING', 6);

INSERT IGNORE INTO order_items
(course_title, price, discount_amount, final_price, order_id, course_id)
VALUES
    ('ChatGPT로 업무 자동화 완성하기', 49000, 5000, 44000, 1, 1),
    ('프롬프트 엔지니어링 마스터 클래스', 59000, 5000, 54000, 1, 2),
    ('Midjourney로 AI 이미지 생성 입문', 39000, 0, 39000, 2, 3),
    ('ChatGPT로 업무 자동화 완성하기', 49000, 0, 49000, 3, 1);

INSERT IGNORE INTO payments
(amount, status, paid_at, order_id, user_id)
VALUES
    (98000, 'PAID', NOW(), 1, 4),
    (39000, 'PAID', NOW(), 2, 5),
    (39000, 'READY', NULL, 3, 6);

INSERT IGNORE INTO enrollments
(enrollment_type, progress_rate, is_completed, enrolled_at, completed_at, order_item_id, course_id, user_id)
VALUES
    ('PAID', 66.67, FALSE, NOW(), NULL, 1, 1, 4),
    ('PAID', 20.00, FALSE, NOW(), NULL, 2, 2, 4),
    ('PAID', 100.00, TRUE, NOW(), NOW(), 3, 3, 5);

INSERT IGNORE INTO notification_settings
(qna_notification, payment_notification, marketing_notification, email_enabled, push_enabled, user_id)
VALUES
    (TRUE, TRUE, FALSE, TRUE, TRUE, 4),
    (TRUE, TRUE, TRUE, TRUE, TRUE, 5),
    (TRUE, FALSE, FALSE, TRUE, FALSE, 6),
    (TRUE, TRUE, FALSE, TRUE, TRUE, 2),
    (TRUE, TRUE, FALSE, TRUE, TRUE, 3);
