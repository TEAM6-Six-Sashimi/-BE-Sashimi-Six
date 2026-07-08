-- 반려(REJECTED) 후 재신청을 허용하기 위해 unique constraint 변경
-- 기존: user_id 전체 unique → REJECTED 상태에서도 재신청(재INSERT) 시 unique 위반 발생
-- 변경: PENDING/APPROVED 상태일 때만 unique 적용 (REJECTED는 NULL 처리 → unique 제약 비적용)

-- 1. FK 지원용 일반 인덱스 조건부 추가 (이미 존재하면 skip)
SET @idx_exists = (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'instructor_profiles'
      AND INDEX_NAME = 'idx_instructor_profiles_user_id'
);
SET @sql = IF(@idx_exists > 0, 'SELECT 1', 'CREATE INDEX idx_instructor_profiles_user_id ON instructor_profiles (user_id)');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 기존 unique constraint 삭제
ALTER TABLE instructor_profiles DROP INDEX user_id;

-- 3. PENDING/APPROVED 상태일 때만 unique 적용되는 생성 컬럼 추가
--    REJECTED는 NULL로 처리되어 unique 제약 비적용 (NULL은 중복으로 보지 않음)
ALTER TABLE instructor_profiles
    ADD COLUMN active_unique_user_id BIGINT AS (
        IF(approval_status IN ('PENDING','APPROVED'), user_id, NULL)
    ) STORED;

-- 4. 새 unique index 추가
ALTER TABLE instructor_profiles
    ADD UNIQUE INDEX uq_active_instructor_profile (active_unique_user_id);
