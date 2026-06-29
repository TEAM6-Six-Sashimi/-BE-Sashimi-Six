-- 소프트 삭제 후 재작성을 허용하기 위해 unique constraint 변경
-- 기존: (user_id, course_id) 전체 unique → DELETED 행이 재작성을 막는 문제
-- 변경: ACTIVE 상태일 때만 unique 적용 (DELETED는 NULL 처리 → unique 제약 비적용)

-- 1. FK 지원용 일반 인덱스 조건부 추가 (이미 존재하면 skip)
SET @idx_exists = (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'reviews'
      AND INDEX_NAME = 'idx_reviews_user_id'
);
SET @sql = IF(@idx_exists > 0, 'SELECT 1', 'CREATE INDEX idx_reviews_user_id ON reviews (user_id)');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 기존 unique constraint 삭제
ALTER TABLE reviews DROP INDEX uq_review_user_course;

-- 3. ACTIVE 상태일 때만 unique 적용되는 생성 컬럼 추가
--    DELETED는 NULL로 처리되어 unique 제약 비적용 (NULL은 중복으로 보지 않음)
ALTER TABLE reviews
    ADD COLUMN active_unique_key VARCHAR(100) AS (
        IF(status = 'ACTIVE', CONCAT(user_id, '_', course_id), NULL)
    ) STORED;

-- 4. 새 unique index 추가
ALTER TABLE reviews
    ADD UNIQUE INDEX uq_active_review (active_unique_key);
