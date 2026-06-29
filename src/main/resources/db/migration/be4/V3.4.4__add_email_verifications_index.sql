CREATE INDEX idx_email_verifications_email_purpose_created
    ON email_verifications (target_email, purpose, created_at DESC);
