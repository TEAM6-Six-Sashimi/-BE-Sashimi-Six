CREATE TABLE review_reports (
    report_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id   BIGINT       NOT NULL,
    reporter_id BIGINT       NOT NULL,
    category    VARCHAR(20)  NOT NULL,
    reason      VARCHAR(200),
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at  DATETIME     NOT NULL,
    CONSTRAINT uq_review_report UNIQUE (review_id, reporter_id),
    CONSTRAINT fk_review_report_review FOREIGN KEY (review_id) REFERENCES reviews (review_id)
);
