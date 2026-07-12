ALTER TABLE credit_charge_payments
    ADD COLUMN retry_count INT NOT NULL DEFAULT 0,
    ADD COLUMN last_retried_at DATETIME NULL;

CREATE INDEX idx_credit_charge_payments_recovery
    ON credit_charge_payments (status, retry_count, requested_at);