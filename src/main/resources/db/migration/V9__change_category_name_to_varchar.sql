-- 강의 카테고리,상세정보 더이데이터 변경

ALTER TABLE categories MODIFY COLUMN name VARCHAR(100) NOT NULL;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE course_sessions;
TRUNCATE TABLE courses;
TRUNCATE TABLE categories;
SET FOREIGN_KEY_CHECKS = 1;


INSERT IGNORE INTO categories (main_category_id, name, sub_category, sort_order, is_active)
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



INSERT INTO courses
(title, description, price, difficulty, thumbnail, total_duration, status, reject_reason, rating_avg, review_count, student_count, created_at, approved_at, category_id, instructor_id)
VALUES
    -- IT·정보통신
    ('정보처리기사 필기 완전정복', '최신 출제기준에 맞춘 정보처리기사 필기 핵심 이론과 기출문제 풀이 강의입니다.', 55000, 'INTERMEDIATE', 'thumbnails/it_cert.jpg', 480, 'APPROVED', NULL, 4.80, 312, 2100, '2025-01-05 09:00:00', '2025-01-07 10:00:00', 1, 2),
    ('정보처리기사 실기 단기합격', '실기 시험에 자주 나오는 핵심 알고리즘과 SQL을 집중적으로 학습합니다.', 65000, 'INTERMEDIATE', 'thumbnails/it_cert2.jpg', 560, 'APPROVED', NULL, 4.75, 278, 1850, '2025-01-08 09:00:00', '2025-01-10 10:00:00', 1, 3),
    ('정보보안기사 합격 로드맵', '보안 이론부터 실습까지, 정보보안기사 자격증 취득을 위한 체계적인 커리큘럼입니다.', 70000, 'ADVANCED', 'thumbnails/security_cert.jpg', 620, 'APPROVED', NULL, 4.82, 195, 980, '2025-01-12 09:00:00', '2025-01-14 10:00:00', 2, 2),
    ('네트워크관리사 2급 한번에 끝내기', '네트워크 기초 개념부터 실전 문제까지 네트워크관리사 2급 시험 대비 완성 강의입니다.', 45000, 'BEGINNER', 'thumbnails/network_cert.jpg', 390, 'APPROVED', NULL, 4.70, 143, 760, '2025-01-15 09:00:00', '2025-01-17 10:00:00', 3, 3),
    ('빅데이터분석기사 필기+실기 패키지', 'R과 Python을 활용한 빅데이터 분석 실무 중심의 자격증 취득 강의입니다.', 80000, 'ADVANCED', 'thumbnails/bigdata_cert.jpg', 700, 'APPROVED', NULL, 4.88, 224, 1340, '2025-01-18 09:00:00', '2025-01-20 10:00:00', 4, 2),
    ('ADsP 데이터분석 준전문가 단기완성', 'ADsP 시험의 핵심 개념과 기출 유형을 빠르게 정리하는 단기 합격 전략 강의입니다.', 40000, 'BEGINNER', 'thumbnails/adsp_cert.jpg', 300, 'APPROVED', NULL, 4.72, 168, 920, '2025-01-20 09:00:00', '2025-01-22 10:00:00', 5, 3),
    ('SQLD 자격증 SQL 기초부터 합격까지', 'SQL 기본 문법부터 SQLD 시험 핵심 포인트까지 한 번에 정리하는 강의입니다.', 42000, 'BEGINNER', 'thumbnails/sqld_cert.jpg', 350, 'APPROVED', NULL, 4.78, 201, 1150, '2025-01-22 09:00:00', '2025-01-24 10:00:00', 6, 2),

    -- 경영·회계
    ('전산회계 1급 실전 완성', 'KcLep 프로그램 실습과 함께 전산회계 1급 이론을 완벽하게 정리하는 강의입니다.', 48000, 'INTERMEDIATE', 'thumbnails/accounting_cert.jpg', 420, 'APPROVED', NULL, 4.76, 185, 1020, '2025-02-01 09:00:00', '2025-02-03 10:00:00', 7, 3),
    ('전산회계 2급 초보자 입문', '회계 지식이 전혀 없어도 시작할 수 있는 전산회계 2급 입문 강의입니다.', 35000, 'BEGINNER', 'thumbnails/accounting_cert2.jpg', 280, 'APPROVED', NULL, 4.65, 142, 880, '2025-02-03 09:00:00', '2025-02-05 10:00:00', 7, 2),
    ('재경관리사 재무회계·세무·원가 통합반', '재무회계, 세무회계, 원가관리회계를 한 번에 준비하는 재경관리사 합격 강의입니다.', 75000, 'ADVANCED', 'thumbnails/finance_cert.jpg', 680, 'APPROVED', NULL, 4.83, 167, 740, '2025-02-05 09:00:00', '2025-02-07 10:00:00', 8, 3),
    ('물류관리사 한번에 합격하기', '물류 관련 법규와 실무를 체계적으로 정리한 물류관리사 자격증 대비 강의입니다.', 50000, 'INTERMEDIATE', 'thumbnails/logistics_cert.jpg', 440, 'APPROVED', NULL, 4.68, 123, 610, '2025-02-08 09:00:00', '2025-02-10 10:00:00', 9, 2),
    ('사회조사분석사 2급 핵심정리', '설문 설계부터 데이터 분석까지, 사회조사분석사 2급 합격을 위한 핵심 강의입니다.', 45000, 'INTERMEDIATE', 'thumbnails/social_cert.jpg', 380, 'APPROVED', NULL, 4.71, 98, 520, '2025-02-10 09:00:00', '2025-02-12 10:00:00', 10, 3),
    ('유통관리사 2급 단기 합격전략', '유통 산업의 핵심 이론과 최신 기출문제를 바탕으로 단기 합격을 목표로 하는 강의입니다.', 42000, 'BEGINNER', 'thumbnails/distribution_cert.jpg', 320, 'APPROVED', NULL, 4.66, 115, 670, '2025-02-12 09:00:00', '2025-02-14 10:00:00', 11, 2),

    -- 디자인
    ('컴퓨터그래픽스운용기능사 실기 완성', '포토샵·일러스트레이터를 활용한 컴퓨터그래픽스운용기능사 실기 합격 강의입니다.', 55000, 'INTERMEDIATE', 'thumbnails/cg_cert.jpg', 500, 'APPROVED', NULL, 4.79, 178, 890, '2025-03-01 09:00:00', '2025-03-03 10:00:00', 12, 3),
    ('시각디자인산업기사 이론+실기 패키지', '시각디자인의 이론 기초부터 실기 작품 제작까지 완성하는 종합 강의입니다.', 65000, 'INTERMEDIATE', 'thumbnails/visual_cert.jpg', 580, 'APPROVED', NULL, 4.74, 134, 620, '2025-03-05 09:00:00', '2025-03-07 10:00:00', 13, 2),
    ('웹디자인기능사 HTML·CSS 실전', 'HTML, CSS, 자바스크립트를 활용한 웹디자인기능사 실기 완벽 대비 강의입니다.', 50000, 'BEGINNER', 'thumbnails/web_cert.jpg', 420, 'APPROVED', NULL, 4.81, 209, 1100, '2025-03-08 09:00:00', '2025-03-10 10:00:00', 14, 3),
    ('실내건축기능사 도면 실기 완성', '실내 공간 설계와 도면 작성법을 중심으로 실내건축기능사 합격을 준비하는 강의입니다.', 58000, 'INTERMEDIATE', 'thumbnails/interior_cert.jpg', 460, 'APPROVED', NULL, 4.69, 112, 530, '2025-03-12 09:00:00', '2025-03-14 10:00:00', 15, 2),

    -- 건설·안전
    ('산업안전기사 필기 핵심 요약', '산업안전 법규와 기준을 빠르게 정리하고 기출 문제로 실전 감각을 키우는 강의입니다.', 52000, 'INTERMEDIATE', 'thumbnails/safety_cert.jpg', 430, 'APPROVED', NULL, 4.77, 156, 840, '2025-04-01 09:00:00', '2025-04-03 10:00:00', 16, 3),
    ('건설안전기사 이론 완전정복', '건설현장 안전관리 이론과 관계 법령을 체계적으로 학습하는 강의입니다.', 55000, 'INTERMEDIATE', 'thumbnails/construction_cert.jpg', 470, 'APPROVED', NULL, 4.72, 132, 680, '2025-04-05 09:00:00', '2025-04-07 10:00:00', 17, 2),
    ('소방설비기사 전기·기계 통합반', '소방설비기사 전기분야와 기계분야를 함께 준비하는 통합 합격 전략 강의입니다.', 70000, 'ADVANCED', 'thumbnails/fire_cert.jpg', 640, 'APPROVED', NULL, 4.80, 148, 720, '2025-04-08 09:00:00', '2025-04-10 10:00:00', 18, 3),
    ('위험물산업기사 단기 합격 전략', '위험물 관련 법규와 화학 이론을 집중 정리한 위험물산업기사 대비 강의입니다.', 45000, 'INTERMEDIATE', 'thumbnails/hazmat_cert.jpg', 360, 'APPROVED', NULL, 4.67, 104, 510, '2025-04-12 09:00:00', '2025-04-14 10:00:00', 19, 2),

    -- 식품·조리
    ('한식조리기능사 실기 완성', '한식 조리 실기 시험의 20가지 과제를 하나씩 완벽하게 정복하는 강의입니다.', 48000, 'BEGINNER', 'thumbnails/korean_cook.jpg', 400, 'APPROVED', NULL, 4.85, 267, 1580, '2025-05-01 09:00:00', '2025-05-03 10:00:00', 20, 3),
    ('양식조리기능사 실기 마스터', '양식 조리 실기 과제 전 메뉴를 반복 실습하며 합격 실력을 키우는 강의입니다.', 48000, 'BEGINNER', 'thumbnails/western_cook.jpg', 380, 'APPROVED', NULL, 4.78, 198, 1120, '2025-05-03 09:00:00', '2025-05-05 10:00:00', 20, 2),
    ('제과기능사 실기 완벽 대비', '제과 실기 시험 과제별 레시피와 핵심 포인트를 집중 정리한 합격 강의입니다.', 50000, 'BEGINNER', 'thumbnails/confectionery_cert.jpg', 410, 'APPROVED', NULL, 4.82, 215, 1230, '2025-05-05 09:00:00', '2025-05-07 10:00:00', 21, 3),
    ('제빵기능사 실기 한번에 합격', '빵 반죽부터 완성까지, 제빵기능사 실기 전 과제를 체계적으로 학습하는 강의입니다.', 50000, 'BEGINNER', 'thumbnails/baking_cert.jpg', 410, 'APPROVED', NULL, 4.80, 187, 1050, '2025-05-08 09:00:00', '2025-05-10 10:00:00', 22, 2),
    ('식품기사 필기 핵심이론 총정리', '식품화학, 식품위생, 식품가공 등 필기 핵심 이론을 체계적으로 정리하는 강의입니다.', 58000, 'INTERMEDIATE', 'thumbnails/food_cert.jpg', 490, 'APPROVED', NULL, 4.73, 143, 710, '2025-05-12 09:00:00', '2025-05-14 10:00:00', 23, 3),
    ('영양사 국가시험 합격 전략', '영양사 국가시험 전 과목을 체계적으로 정리하고 기출문제로 실전 대비하는 강의입니다.', 75000, 'ADVANCED', 'thumbnails/nutritionist_cert.jpg', 660, 'APPROVED', NULL, 4.84, 176, 820, '2025-05-15 09:00:00', '2025-05-17 10:00:00', 24, 2),

    -- 부동산·금융
    ('공인중개사 1차 민법 완전정복', '공인중개사 1차 시험의 핵심 과목인 민법 및 민사특별법을 집중 학습하는 강의입니다.', 80000, 'ADVANCED', 'thumbnails/realtor_cert1.jpg', 720, 'APPROVED', NULL, 4.86, 345, 2300, '2025-06-01 09:00:00', '2025-06-03 10:00:00', 25, 3),
    ('공인중개사 2차 부동산공법 완성', '공인중개사 2차 시험 부동산공법과 중개사법을 핵심 위주로 정리하는 강의입니다.', 80000, 'ADVANCED', 'thumbnails/realtor_cert2.jpg', 700, 'APPROVED', NULL, 4.83, 312, 2100, '2025-06-03 09:00:00', '2025-06-05 10:00:00', 25, 2),
    ('주택관리사 1차 회계원리·시설개론', '주택관리사 1차 시험 핵심 과목을 한 번에 정리하는 단기 합격 강의입니다.', 72000, 'ADVANCED', 'thumbnails/housing_cert.jpg', 650, 'APPROVED', NULL, 4.79, 198, 1050, '2025-06-08 09:00:00', '2025-06-10 10:00:00', 26, 3),
    ('감정평가사 1차 경제학원론 기초', '감정평가사 1차 시험 경제학원론을 기초부터 심화까지 완벽하게 준비하는 강의입니다.', 90000, 'ADVANCED', 'thumbnails/appraiser_cert.jpg', 780, 'APPROVED', NULL, 4.87, 134, 580, '2025-06-12 09:00:00', '2025-06-14 10:00:00', 27, 2),

    -- 어학
    ('한국어교원자격증 2급 취득 완성', '외국어로서의 한국어 교육 이론과 실습을 통해 한국어교원자격증 2급을 취득하는 강의입니다.', 68000, 'INTERMEDIATE', 'thumbnails/korean_teacher.jpg', 580, 'APPROVED', NULL, 4.81, 167, 780, '2025-07-01 09:00:00', '2025-07-03 10:00:00', 28, 3),
    ('관광통역안내사 영어 필기+면접 완성', '관광법규, 관광학개론부터 영어 면접까지 관광통역안내사 합격의 모든 것을 담은 강의입니다.', 75000, 'ADVANCED', 'thumbnails/tour_guide.jpg', 640, 'APPROVED', NULL, 4.83, 145, 670, '2025-07-05 09:00:00', '2025-07-07 10:00:00', 29, 2);

INSERT IGNORE INTO course_sessions
(session_uid, title, video_url, duration_seconds, session_order, is_preview, course_id)
VALUES
    -- course 1: 정보처리기사 필기 완전정복
    (UUID(), '오리엔테이션 및 강의 안내', '/videos/course1/session1.mp4', 1200, 1, TRUE, 1),
    (UUID(), '소프트웨어 설계 핵심 이론', '/videos/course1/session2.mp4', 2100, 2, FALSE, 1),
    (UUID(), '데이터베이스 기출 풀이', '/videos/course1/session3.mp4', 2400, 3, FALSE, 1),
    (UUID(), '운영체제 핵심 요약', '/videos/course1/session4.mp4', 2000, 4, FALSE, 1),
    (UUID(), '최신 기출문제 특강', '/videos/course1/session5.mp4', 2700, 5, FALSE, 1),

    -- course 2: 정보처리기사 실기 단기합격
    (UUID(), '실기 시험 안내 및 전략', '/videos/course2/session1.mp4', 1200, 1, TRUE, 2),
    (UUID(), '알고리즘 핵심 패턴 정리', '/videos/course2/session2.mp4', 2400, 2, FALSE, 2),
    (UUID(), 'SQL 실기 집중 실습', '/videos/course2/session3.mp4', 2700, 3, FALSE, 2),
    (UUID(), '프로그래밍 언어 활용', '/videos/course2/session4.mp4', 2100, 4, FALSE, 2),

    -- course 3: 정보보안기사 합격 로드맵
    (UUID(), '정보보안 개요 및 시험 안내', '/videos/course3/session1.mp4', 1200, 1, TRUE, 3),
    (UUID(), '시스템 보안 핵심 이론', '/videos/course3/session2.mp4', 2400, 2, FALSE, 3),
    (UUID(), '네트워크 보안 실습', '/videos/course3/session3.mp4', 2700, 3, FALSE, 3),
    (UUID(), '보안 법규 및 관리', '/videos/course3/session4.mp4', 2100, 4, FALSE, 3),

    -- course 4: 네트워크관리사 2급 한번에 끝내기
    (UUID(), '네트워크 기초 개념', '/videos/course4/session1.mp4', 1500, 1, TRUE, 4),
    (UUID(), 'TCP/IP 프로토콜 이해', '/videos/course4/session2.mp4', 2100, 2, FALSE, 4),
    (UUID(), '네트워크 장비 설정 실습', '/videos/course4/session3.mp4', 2400, 3, FALSE, 4),

    -- course 5: 빅데이터분석기사 필기+실기 패키지
    (UUID(), '빅데이터 분석 개요', '/videos/course5/session1.mp4', 1500, 1, TRUE, 5),
    (UUID(), 'R 기초 데이터 분석', '/videos/course5/session2.mp4', 2400, 2, FALSE, 5),
    (UUID(), 'Python 머신러닝 실습', '/videos/course5/session3.mp4', 2700, 3, FALSE, 5),
    (UUID(), '실기 모의고사 풀이', '/videos/course5/session4.mp4', 2100, 4, FALSE, 5),

    -- course 6: ADsP 데이터분석 준전문가 단기완성
    (UUID(), 'ADsP 시험 소개 및 전략', '/videos/course6/session1.mp4', 1200, 1, TRUE, 6),
    (UUID(), '데이터 이해 및 처리 기술', '/videos/course6/session2.mp4', 2100, 2, FALSE, 6),
    (UUID(), '데이터 분석 기출 특강', '/videos/course6/session3.mp4', 2400, 3, FALSE, 6),

    -- course 7: SQLD 자격증 SQL 기초부터 합격까지
    (UUID(), 'SQL 기초 문법 이해', '/videos/course7/session1.mp4', 1800, 1, TRUE, 7),
    (UUID(), 'DDL·DML·DCL 완전정복', '/videos/course7/session2.mp4', 2100, 2, FALSE, 7),
    (UUID(), '조인·서브쿼리 실습', '/videos/course7/session3.mp4', 2400, 3, FALSE, 7),
    (UUID(), 'SQLD 기출문제 풀이', '/videos/course7/session4.mp4', 2700, 4, FALSE, 7),

    -- course 8: 전산회계 1급 실전 완성
    (UUID(), '전산회계 시험 안내', '/videos/course8/session1.mp4', 1200, 1, TRUE, 8),
    (UUID(), '재무회계 핵심 이론', '/videos/course8/session2.mp4', 2100, 2, FALSE, 8),
    (UUID(), 'KcLep 프로그램 실습', '/videos/course8/session3.mp4', 2400, 3, FALSE, 8),
    (UUID(), '기출문제 실전 풀이', '/videos/course8/session4.mp4', 2100, 4, FALSE, 8),

    -- course 9: 전산회계 2급 초보자 입문
    (UUID(), '회계의 기초 개념', '/videos/course9/session1.mp4', 1500, 1, TRUE, 9),
    (UUID(), '분개와 전표 작성', '/videos/course9/session2.mp4', 1800, 2, FALSE, 9),
    (UUID(), 'KcLep 입문 실습', '/videos/course9/session3.mp4', 2100, 3, FALSE, 9),

    -- course 10: 재경관리사 재무회계·세무·원가 통합반
    (UUID(), '재경관리사 시험 전략', '/videos/course10/session1.mp4', 1200, 1, TRUE, 10),
    (UUID(), '재무회계 핵심 정리', '/videos/course10/session2.mp4', 2400, 2, FALSE, 10),
    (UUID(), '세무회계 실전 풀이', '/videos/course10/session3.mp4', 2700, 3, FALSE, 10),
    (UUID(), '원가관리회계 완성', '/videos/course10/session4.mp4', 2400, 4, FALSE, 10),

    -- course 11: 물류관리사 한번에 합격하기
    (UUID(), '물류관리 기초 개념', '/videos/course11/session1.mp4', 1500, 1, TRUE, 11),
    (UUID(), '물류 관련 법규 정리', '/videos/course11/session2.mp4', 2100, 2, FALSE, 11),
    (UUID(), '기출문제 집중 풀이', '/videos/course11/session3.mp4', 2400, 3, FALSE, 11),

    -- course 12: 사회조사분석사 2급 핵심정리
    (UUID(), '조사 방법론 기초', '/videos/course12/session1.mp4', 1500, 1, TRUE, 12),
    (UUID(), '설문 설계 및 표본 추출', '/videos/course12/session2.mp4', 2100, 2, FALSE, 12),
    (UUID(), '통계 분석 실습', '/videos/course12/session3.mp4', 2400, 3, FALSE, 12),

    -- course 13: 유통관리사 2급 단기 합격전략
    (UUID(), '유통 산업 이해', '/videos/course13/session1.mp4', 1500, 1, TRUE, 13),
    (UUID(), '유통 마케팅 핵심 이론', '/videos/course13/session2.mp4', 2100, 2, FALSE, 13),
    (UUID(), '기출문제 실전 특강', '/videos/course13/session3.mp4', 2400, 3, FALSE, 13),

    -- course 14: 컴퓨터그래픽스운용기능사 실기 완성
    (UUID(), '포토샵 기초 및 시험 안내', '/videos/course14/session1.mp4', 1500, 1, TRUE, 14),
    (UUID(), '일러스트레이터 핵심 기능', '/videos/course14/session2.mp4', 2100, 2, FALSE, 14),
    (UUID(), '실기 과제 실전 풀이', '/videos/course14/session3.mp4', 2700, 3, FALSE, 14),
    (UUID(), '시험 전 최종 점검', '/videos/course14/session4.mp4', 1800, 4, FALSE, 14),

    -- course 15: 시각디자인산업기사 이론+실기 패키지
    (UUID(), '시각디자인 개론', '/videos/course15/session1.mp4', 1500, 1, TRUE, 15),
    (UUID(), '색채 이론 및 타이포그래피', '/videos/course15/session2.mp4', 2100, 2, FALSE, 15),
    (UUID(), '실기 작품 제작 실습', '/videos/course15/session3.mp4', 2700, 3, FALSE, 15),

    -- course 16: 웹디자인기능사 HTML·CSS 실전
    (UUID(), 'HTML 기초 구조 이해', '/videos/course16/session1.mp4', 1800, 1, TRUE, 16),
    (UUID(), 'CSS 레이아웃 실습', '/videos/course16/session2.mp4', 2100, 2, FALSE, 16),
    (UUID(), '자바스크립트 기초 활용', '/videos/course16/session3.mp4', 2400, 3, FALSE, 16),
    (UUID(), '실기 과제 완성 실습', '/videos/course16/session4.mp4', 2700, 4, FALSE, 16),

    -- course 17: 실내건축기능사 도면 실기 완성
    (UUID(), '실내 공간 설계 기초', '/videos/course17/session1.mp4', 1500, 1, TRUE, 17),
    (UUID(), '도면 작성법 실습', '/videos/course17/session2.mp4', 2400, 2, FALSE, 17),
    (UUID(), '실기 과제 전 유형 정리', '/videos/course17/session3.mp4', 2700, 3, FALSE, 17),

    -- course 18: 산업안전기사 필기 핵심 요약
    (UUID(), '산업안전 개론 및 시험 안내', '/videos/course18/session1.mp4', 1500, 1, TRUE, 18),
    (UUID(), '안전 법규 핵심 정리', '/videos/course18/session2.mp4', 2100, 2, FALSE, 18),
    (UUID(), '기출문제 실전 풀이', '/videos/course18/session3.mp4', 2400, 3, FALSE, 18),

    -- course 19: 건설안전기사 이론 완전정복
    (UUID(), '건설안전 기초 이론', '/videos/course19/session1.mp4', 1500, 1, TRUE, 19),
    (UUID(), '건설현장 안전관리 실무', '/videos/course19/session2.mp4', 2400, 2, FALSE, 19),
    (UUID(), '관계 법령 집중 정리', '/videos/course19/session3.mp4', 2100, 3, FALSE, 19),

    -- course 20: 소방설비기사 전기·기계 통합반
    (UUID(), '소방설비 개요 및 법규', '/videos/course20/session1.mp4', 1500, 1, TRUE, 20),
    (UUID(), '전기 분야 핵심 이론', '/videos/course20/session2.mp4', 2400, 2, FALSE, 20),
    (UUID(), '기계 분야 핵심 이론', '/videos/course20/session3.mp4', 2400, 3, FALSE, 20),
    (UUID(), '실전 기출문제 풀이', '/videos/course20/session4.mp4', 2700, 4, FALSE, 20),

    -- course 21: 위험물산업기사 단기 합격 전략
    (UUID(), '위험물 개론 및 시험 안내', '/videos/course21/session1.mp4', 1500, 1, TRUE, 21),
    (UUID(), '위험물 화학 이론 정리', '/videos/course21/session2.mp4', 2100, 2, FALSE, 21),
    (UUID(), '관련 법규 및 기출 풀이', '/videos/course21/session3.mp4', 2400, 3, FALSE, 21),

    -- course 22: 한식조리기능사 실기 완성
    (UUID(), '한식 실기 시험 안내', '/videos/course22/session1.mp4', 1200, 1, TRUE, 22),
    (UUID(), '국·탕·찌개류 조리 실습', '/videos/course22/session2.mp4', 2100, 2, FALSE, 22),
    (UUID(), '구이·볶음·전류 실습', '/videos/course22/session3.mp4', 2400, 3, FALSE, 22),
    (UUID(), '김치·무침·회류 실습', '/videos/course22/session4.mp4', 2100, 4, FALSE, 22),

    -- course 23: 양식조리기능사 실기 마스터
    (UUID(), '양식 실기 시험 안내', '/videos/course23/session1.mp4', 1200, 1, TRUE, 23),
    (UUID(), '수프·샐러드 조리 실습', '/videos/course23/session2.mp4', 2100, 2, FALSE, 23),
    (UUID(), '메인 디시 조리 실습', '/videos/course23/session3.mp4', 2400, 3, FALSE, 23),

    -- course 24: 제과기능사 실기 완벽 대비
    (UUID(), '제과 실기 시험 안내', '/videos/course24/session1.mp4', 1200, 1, TRUE, 24),
    (UUID(), '케이크류 제조 실습', '/videos/course24/session2.mp4', 2400, 2, FALSE, 24),
    (UUID(), '쿠키·타르트류 실습', '/videos/course24/session3.mp4', 2100, 3, FALSE, 24),

    -- course 25: 제빵기능사 실기 한번에 합격
    (UUID(), '제빵 실기 시험 안내', '/videos/course25/session1.mp4', 1200, 1, TRUE, 25),
    (UUID(), '식빵·단과자빵 실습', '/videos/course25/session2.mp4', 2400, 2, FALSE, 25),
    (UUID(), '특수빵류 제조 실습', '/videos/course25/session3.mp4', 2100, 3, FALSE, 25),

    -- course 26: 식품기사 필기 핵심이론 총정리
    (UUID(), '식품기사 시험 안내', '/videos/course26/session1.mp4', 1500, 1, TRUE, 26),
    (UUID(), '식품화학 핵심 이론', '/videos/course26/session2.mp4', 2400, 2, FALSE, 26),
    (UUID(), '식품위생·가공 정리', '/videos/course26/session3.mp4', 2700, 3, FALSE, 26),

    -- course 27: 영양사 국가시험 합격 전략
    (UUID(), '영양사 국가시험 안내', '/videos/course27/session1.mp4', 1500, 1, TRUE, 27),
    (UUID(), '영양학·생화학 핵심 정리', '/videos/course27/session2.mp4', 2700, 2, FALSE, 27),
    (UUID(), '식사요법·임상영양 실전', '/videos/course27/session3.mp4', 2400, 3, FALSE, 27),
    (UUID(), '기출문제 집중 특강', '/videos/course27/session4.mp4', 2700, 4, FALSE, 27),

    -- course 28: 공인중개사 1차 민법 완전정복
    (UUID(), '민법 총칙 기초 이론', '/videos/course28/session1.mp4', 1800, 1, TRUE, 28),
    (UUID(), '물권법 핵심 정리', '/videos/course28/session2.mp4', 2700, 2, FALSE, 28),
    (UUID(), '계약법·채권법 실전', '/videos/course28/session3.mp4', 2700, 3, FALSE, 28),
    (UUID(), '민사특별법 기출 풀이', '/videos/course28/session4.mp4', 2400, 4, FALSE, 28),

    -- course 29: 공인중개사 2차 부동산공법 완성
    (UUID(), '부동산공법 개요', '/videos/course29/session1.mp4', 1800, 1, TRUE, 29),
    (UUID(), '국토계획법 핵심 정리', '/videos/course29/session2.mp4', 2700, 2, FALSE, 29),
    (UUID(), '중개사법 실전 풀이', '/videos/course29/session3.mp4', 2400, 3, FALSE, 29),

    -- course 30: 주택관리사 1차 회계원리·시설개론
    (UUID(), '주택관리사 시험 전략', '/videos/course30/session1.mp4', 1500, 1, TRUE, 30),
    (UUID(), '회계원리 핵심 이론', '/videos/course30/session2.mp4', 2700, 2, FALSE, 30),
    (UUID(), '시설개론 핵심 정리', '/videos/course30/session3.mp4', 2400, 3, FALSE, 30),
    (UUID(), '기출문제 실전 특강', '/videos/course30/session4.mp4', 2100, 4, FALSE, 30),

    -- course 31: 감정평가사 1차 경제학원론 기초
    (UUID(), '경제학 기초 개념 이해', '/videos/course31/session1.mp4', 1800, 1, TRUE, 31),
    (UUID(), '미시경제학 핵심 정리', '/videos/course31/session2.mp4', 2700, 2, FALSE, 31),
    (UUID(), '거시경제학 실전 풀이', '/videos/course31/session3.mp4', 2700, 3, FALSE, 31),
    (UUID(), '기출문제 집중 특강', '/videos/course31/session4.mp4', 2400, 4, FALSE, 31),

    -- course 32: 한국어교원자격증 2급 취득 완성
    (UUID(), '한국어 교육 이론 개요', '/videos/course32/session1.mp4', 1500, 1, TRUE, 32),
    (UUID(), '한국어 문법 교수법', '/videos/course32/session2.mp4', 2400, 2, FALSE, 32),
    (UUID(), '한국 문화 교육 실습', '/videos/course32/session3.mp4', 2100, 3, FALSE, 32),
    (UUID(), '모의 수업 및 평가', '/videos/course32/session4.mp4', 2400, 4, FALSE, 32),

    -- course 33: 관광통역안내사 영어 필기+면접 완성
    (UUID(), '관광법규 핵심 정리', '/videos/course33/session1.mp4', 1800, 1, TRUE, 33),
    (UUID(), '관광학개론 이론 완성', '/videos/course33/session2.mp4', 2400, 2, FALSE, 33),
    (UUID(), '영어 면접 실전 대비', '/videos/course33/session3.mp4', 2700, 3, FALSE, 33),
    (UUID(), '모의 면접 특강', '/videos/course33/session4.mp4', 2100, 4, FALSE, 33);