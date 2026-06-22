-- 기존 구독 타입을 신규 플랜으로 변환하기 위한 임시 확장
ALTER TABLE subscriptions
    MODIFY COLUMN type ENUM(
    'BASIC',
    'PREMIUM',
    'PRO',
    'MONTHLY',
    'ANNUAL'
    ) NOT NULL;

UPDATE subscriptions
SET type = 'MONTHLY'
WHERE type IN ('BASIC', 'PREMIUM');

UPDATE subscriptions
SET type = 'ANNUAL'
WHERE type = 'PRO';

ALTER TABLE subscriptions
    MODIFY COLUMN type ENUM('MONTHLY', 'ANNUAL') NOT NULL;

ALTER TABLE subscriptions
    ADD INDEX idx_subscriptions_user_status (
        user_id,
        status
    ),
    ADD INDEX idx_subscriptions_next_billing (
        status,
        auto_renew,
        next_billing_at
    );

CREATE TABLE subscription_payments (
                                       subscription_payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                       subscription_id BIGINT NOT NULL,
                                       order_id BIGINT NOT NULL,
                                       payment_id BIGINT NOT NULL,
                                       user_id BIGINT NOT NULL,

                                       order_no VARCHAR(100) NOT NULL,
                                       plan_code ENUM('MONTHLY', 'ANNUAL') NOT NULL,
                                       amount BIGINT NOT NULL,
                                       billing_type ENUM('INITIAL', 'RENEWAL') NOT NULL,
                                       paid_at DATETIME NOT NULL,
                                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_subscription_payments_subscription
                                           FOREIGN KEY (subscription_id)
                                               REFERENCES subscriptions(subscription_id),

                                       CONSTRAINT fk_subscription_payments_order
                                           FOREIGN KEY (order_id)
                                               REFERENCES orders(order_id),

                                       CONSTRAINT fk_subscription_payments_payment
                                           FOREIGN KEY (payment_id)
                                               REFERENCES payments(payment_id),

                                       CONSTRAINT fk_subscription_payments_user
                                           FOREIGN KEY (user_id)
                                               REFERENCES users(user_id),

                                       CONSTRAINT uq_subscription_payments_order
                                           UNIQUE (order_id),

                                       CONSTRAINT uq_subscription_payments_payment
                                           UNIQUE (payment_id),

                                       INDEX idx_subscription_payments_user_paid_at (
        user_id,
        paid_at
    ),

                                       INDEX idx_subscription_payments_subscription (
        subscription_id
    )
);