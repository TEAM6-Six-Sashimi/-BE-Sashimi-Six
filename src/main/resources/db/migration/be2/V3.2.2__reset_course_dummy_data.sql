-- =========================================================
-- 강의 관련 더미 데이터 전체 삭제 + AUTO_INCREMENT 초기화
-- 목적: 정합성 깨진 더미 데이터를 비우고 재시딩 준비
-- 주의: 테이블 구조는 유지되고 행(row)만 삭제됨
-- FK 의존성에 따라 자식 -> 부모 순서로 삭제
-- =========================================================

-- [1단계] 말단 자식 (다른 테이블을 참조만 하는 것들)
DELETE FROM review_reports;         -- reviews 참조
DELETE FROM subscription_payments;  -- orders, payments 참조
DELETE FROM coupon_usages;          -- orders 참조
DELETE FROM cart_items;             -- courses 참조
DELETE FROM roadmap_steps;          -- courses 참조
DELETE FROM course_qna;             -- courses, course_sessions 참조
DELETE FROM learning_progress;      -- course_sessions, courses 참조

-- [2단계] 중간 계층
DELETE FROM reviews;                -- courses 참조 (review_reports 삭제 후)
DELETE FROM enrollments;            -- order_items, courses 참조 (order_items보다 먼저)
DELETE FROM payments;               -- orders 참조 (subscription_payments 삭제 후)
DELETE FROM order_items;            -- orders, courses 참조 (enrollments 삭제 후)

-- [3단계] 상위 부모
DELETE FROM orders;                 -- payments/order_items/coupon_usages 삭제 후
DELETE FROM course_sessions;        -- courses 참조 (qna/learning_progress 삭제 후)

-- [4단계] 최상위 부모
DELETE FROM courses;

-- =========================================================
-- AUTO_INCREMENT 초기화 (비운 테이블은 1부터 다시 시작)
-- course_id 를 1부터 시작하도록 재설정
-- =========================================================
ALTER TABLE courses           AUTO_INCREMENT = 1;
ALTER TABLE course_sessions   AUTO_INCREMENT = 1;
ALTER TABLE enrollments       AUTO_INCREMENT = 1;
ALTER TABLE learning_progress AUTO_INCREMENT = 1;
ALTER TABLE reviews           AUTO_INCREMENT = 1;
ALTER TABLE orders            AUTO_INCREMENT = 1;
ALTER TABLE order_items       AUTO_INCREMENT = 1;
ALTER TABLE payments          AUTO_INCREMENT = 1;
