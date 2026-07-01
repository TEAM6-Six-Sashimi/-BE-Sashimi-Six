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
