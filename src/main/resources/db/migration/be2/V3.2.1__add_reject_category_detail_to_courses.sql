ALTER TABLE courses
    ADD COLUMN reject_reason_category VARCHAR(50)  NULL AFTER reject_reason,
    ADD COLUMN reject_detail          VARCHAR(500) NULL AFTER reject_reason_category;
