-- 정책 확인 결과 수강평은 삭제 후 재작성이 불가능해야 함 (V3.5.5는 잘못 적용된 변경)
-- V3.5.5에서 추가한 "ACTIVE 상태일 때만 unique 적용" 로직을 걷어내고
-- 원래대로 (user_id, course_id) 전체에 대해 unique 제약을 다시 적용

-- 1. V3.5.5에서 추가한 조건부 unique index 삭제
ALTER TABLE reviews DROP INDEX uq_active_review;

-- 2. V3.5.5에서 추가한 생성 컬럼 삭제
ALTER TABLE reviews DROP COLUMN active_unique_key;

-- 3. V3.5.5에서 추가한 FK 지원용 인덱스 삭제 (더 이상 필요 없음, unique 제약이 대체)
SET @idx_exists = (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'reviews'
      AND INDEX_NAME = 'idx_reviews_user_id'
);
SET @sql = IF(@idx_exists > 0, 'DROP INDEX idx_reviews_user_id ON reviews', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. V3.5.5 정책 하에서는 DELETED 리뷰끼리 unique 제약이 없어 같은 (user_id, course_id)에
--    ACTIVE 1건 + DELETED 여러 건이 쌓여있을 수 있음 -> 바로 unique index를 걸면 중복 데이터로 실패 가능
--    (user_id, course_id) 조합당 가장 최근 리뷰(review_id 최대)만 남기고 나머지 삭제
DELETE r1 FROM reviews r1
    INNER JOIN reviews r2
        ON r1.user_id = r2.user_id
        AND r1.course_id = r2.course_id
        AND r1.review_id < r2.review_id;

-- 5. 원래 unique constraint 복원 (상태 무관, 유저당 강의 1개 리뷰만 허용)
ALTER TABLE reviews
    ADD UNIQUE INDEX uq_review_user_course (user_id, course_id);
