-- 동시 신청 시 같은 (student_id, instructor_id, course_id)로 PENDING/ACCEPTED 커피챗이
-- 중복 생성되는 레이스 컨디션 방지 (exists 체크 + save가 원자적이지 않음)
-- MySQL은 Postgres의 partial unique index(WHERE절)를 지원하지 않으므로
-- PENDING/ACCEPTED일 때만 값이 채워지는 생성 컬럼 + UNIQUE INDEX로 우회

ALTER TABLE coffee_chats
    ADD COLUMN active_unique_flag TINYINT AS (
        IF(status IN ('PENDING', 'ACCEPTED'), 1, NULL)
    ) STORED;

ALTER TABLE coffee_chats
    ADD UNIQUE INDEX uq_active_coffee_chat (student_id, instructor_id, course_id, active_unique_flag);
