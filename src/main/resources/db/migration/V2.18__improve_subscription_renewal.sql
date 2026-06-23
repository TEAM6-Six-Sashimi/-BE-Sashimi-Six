-- ACTIVE 구독은 사용자당 하나만 허용
ALTER TABLE subscriptions
    ADD COLUMN active_user_id BIGINT
        GENERATED ALWAYS AS (
            CASE
                WHEN status = 'ACTIVE' THEN user_id
                ELSE NULL
                END
            ) STORED;

ALTER TABLE subscriptions
    ADD CONSTRAINT uq_subscriptions_active_user
        UNIQUE (active_user_id);

-- 동일 결제 시간에도 안정적인 페이징 정렬 지원
ALTER TABLE subscription_payments
DROP INDEX idx_subscription_payments_user_paid_at,
    ADD INDEX idx_subscription_payments_user_paid_at_id (
        user_id,
        paid_at,
        subscription_payment_id
    );