CREATE TABLE email_outbox
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    to_email    VARCHAR(255) NOT NULL,
    subject     VARCHAR(500) NOT NULL,
    content     TEXT         NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at  DATETIME     NOT NULL,
    sent_at     DATETIME,
    retry_count INT          NOT NULL DEFAULT 0
);
