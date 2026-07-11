CREATE TABLE notices (
                         notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(200) NOT NULL,
                         content TEXT NOT NULL,
                         pinned BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at DATETIME(6) NOT NULL
);

CREATE INDEX idx_notices_pinned_created_at
    ON notices (pinned, created_at, notice_id);