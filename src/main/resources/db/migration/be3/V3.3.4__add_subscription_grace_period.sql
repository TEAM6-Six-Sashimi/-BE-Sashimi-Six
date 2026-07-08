ALTER TABLE subscriptions
    ADD COLUMN grace_period_until DATETIME NULL,
    ADD COLUMN last_renewal_failed_at DATETIME NULL,
    ADD COLUMN renewal_retry_count INT NOT NULL DEFAULT 0;

CREATE INDEX idx_subscriptions_renewal_due
    ON subscriptions (status, auto_renew, next_billing_at);

CREATE INDEX idx_subscriptions_grace_period
    ON subscriptions (status, grace_period_until);