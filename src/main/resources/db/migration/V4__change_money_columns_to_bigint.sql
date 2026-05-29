--
-- V4__change_money_columns_to_bigint.sql
-- 금액/가격/크레딧은 소수점을 사용하지 않으므로 DECIMAL -> BIGINT로 변경한다.
-- 평점, 진행률, AI 점수 같은 비율/점수성 컬럼은 DECIMAL 유지.

ALTER TABLE courses
    MODIFY COLUMN price BIGINT NOT NULL DEFAULT 0;

ALTER TABLE cart_items
    MODIFY COLUMN price BIGINT NOT NULL;

ALTER TABLE orders
    MODIFY COLUMN total_amount BIGINT NOT NULL DEFAULT 0,
    MODIFY COLUMN discount_amount BIGINT NOT NULL DEFAULT 0,
    MODIFY COLUMN final_amount BIGINT NOT NULL DEFAULT 0;

ALTER TABLE order_items
    MODIFY COLUMN price BIGINT NOT NULL,
    MODIFY COLUMN discount_amount BIGINT NOT NULL DEFAULT 0,
    MODIFY COLUMN final_price BIGINT NOT NULL;

ALTER TABLE payments
    MODIFY COLUMN amount BIGINT NOT NULL;

ALTER TABLE credits
    MODIFY COLUMN balance BIGINT NOT NULL DEFAULT 0;

ALTER TABLE coupons
    MODIFY COLUMN discount_value BIGINT NOT NULL,
    MODIFY COLUMN min_order_amount BIGINT NOT NULL DEFAULT 0;

ALTER TABLE coupon_usages
    MODIFY COLUMN discount_amount BIGINT NOT NULL;

ALTER TABLE subscriptions
    MODIFY COLUMN price BIGINT NOT NULL;

ALTER TABLE settlements
    MODIFY COLUMN total_sales BIGINT NOT NULL DEFAULT 0,
    MODIFY COLUMN commission_amount BIGINT NOT NULL DEFAULT 0,
    MODIFY COLUMN settlement_amount BIGINT NOT NULL DEFAULT 0;