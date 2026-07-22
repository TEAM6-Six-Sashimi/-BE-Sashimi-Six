-- k6 부하테스트용 계정 시드 (test31~test80)
-- test31~test60: 일반 계정 (구독권 없음)
-- test61~test80: 구독권(MONTHLY) 활성 계정, AI 기능 테스트용
-- 비밀번호는 test01~test30과 동일 (test01~test30/V3.4.5__insert_test_users.sql 참고)

INSERT INTO users (login_id, email, password, name, birth_date, role, status, email_verified, referral_code)
VALUES
    ('test31', 'test31@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트31', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000031'),
    ('test32', 'test32@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트32', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000032'),
    ('test33', 'test33@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트33', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000033'),
    ('test34', 'test34@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트34', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000034'),
    ('test35', 'test35@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트35', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000035'),
    ('test36', 'test36@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트36', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000036'),
    ('test37', 'test37@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트37', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000037'),
    ('test38', 'test38@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트38', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000038'),
    ('test39', 'test39@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트39', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000039'),
    ('test40', 'test40@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트40', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000040'),
    ('test41', 'test41@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트41', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000041'),
    ('test42', 'test42@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트42', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000042'),
    ('test43', 'test43@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트43', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000043'),
    ('test44', 'test44@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트44', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000044'),
    ('test45', 'test45@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트45', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000045'),
    ('test46', 'test46@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트46', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000046'),
    ('test47', 'test47@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트47', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000047'),
    ('test48', 'test48@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트48', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000048'),
    ('test49', 'test49@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트49', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000049'),
    ('test50', 'test50@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트50', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000050'),
    ('test51', 'test51@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트51', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000051'),
    ('test52', 'test52@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트52', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000052'),
    ('test53', 'test53@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트53', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000053'),
    ('test54', 'test54@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트54', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000054'),
    ('test55', 'test55@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트55', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000055'),
    ('test56', 'test56@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트56', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000056'),
    ('test57', 'test57@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트57', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000057'),
    ('test58', 'test58@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트58', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000058'),
    ('test59', 'test59@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트59', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000059'),
    ('test60', 'test60@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트60', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000060');

INSERT INTO users (login_id, email, password, name, birth_date, role, status, email_verified, referral_code, ai_consent)
VALUES
    ('test61', 'test61@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트61', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000061', TRUE),
    ('test62', 'test62@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트62', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000062', TRUE),
    ('test63', 'test63@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트63', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000063', TRUE),
    ('test64', 'test64@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트64', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000064', TRUE),
    ('test65', 'test65@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트65', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000065', TRUE),
    ('test66', 'test66@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트66', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000066', TRUE),
    ('test67', 'test67@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트67', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000067', TRUE),
    ('test68', 'test68@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트68', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000068', TRUE),
    ('test69', 'test69@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트69', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000069', TRUE),
    ('test70', 'test70@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트70', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000070', TRUE),
    ('test71', 'test71@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트71', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000071', TRUE),
    ('test72', 'test72@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트72', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000072', TRUE),
    ('test73', 'test73@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트73', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000073', TRUE),
    ('test74', 'test74@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트74', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000074', TRUE),
    ('test75', 'test75@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트75', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000075', TRUE),
    ('test76', 'test76@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트76', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000076', TRUE),
    ('test77', 'test77@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트77', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000077', TRUE),
    ('test78', 'test78@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트78', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000078', TRUE),
    ('test79', 'test79@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트79', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000079', TRUE),
    ('test80', 'test80@test.com', '$2b$10$ZHWIOL7PR2ncm/dWgFTYpeZWu1/mtfuBp6Wx1ANAtcLlhcxY7WU8O', '테스트80', '1990-01-01', 'STUDENT', 'ACTIVE', TRUE, 'TEST000080', TRUE);

-- test61~test80: MONTHLY 구독권 ACTIVE 상태로 부여 (1년 후 만료, 결제/스케줄러 영향 없도록 auto_renew=FALSE)
INSERT INTO subscriptions (type, status, price, started_at, expired_at, next_billing_at, auto_renew, user_id)
VALUES
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test61')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test62')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test63')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test64')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test65')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test66')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test67')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test68')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test69')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test70')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test71')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test72')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test73')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test74')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test75')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test76')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test77')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test78')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test79')),
    ('MONTHLY', 'ACTIVE', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), DATE_ADD(NOW(), INTERVAL 1 YEAR), FALSE, (SELECT user_id FROM users WHERE login_id = 'test80'));
