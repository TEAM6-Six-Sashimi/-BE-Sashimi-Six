ALTER TABLE refresh_tokens
    ADD COLUMN issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER token,
    ADD COLUMN last_used_at DATETIME NULL AFTER issued_at,
    ADD COLUMN revoked_at DATETIME NULL AFTER expiry_date,
    ADD COLUMN revoked_reason VARCHAR(50) NULL AFTER revoked_at;
