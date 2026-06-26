CREATE TABLE email_outbox
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    to_email    VARCHAR(255) NOT NULL,
    subject     VARCHAR(500) NOT NULL,
    content     TEXT         NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED')),
    created_at  DATETIME     NOT NULL,
    sent_at     DATETIME,
    retry_count INT          NOT NULL DEFAULT 0
);

CREATE INDEX idx_email_outbox_status ON email_outbox (status);
