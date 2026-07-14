-- =========================================================
-- Module04 Schema Integrated Migration
-- Integrated from be1 ~ be5 V3.x schema migrations
-- =========================================================

-- =========================================================
-- 1. Cleanup before constraints
-- =========================================================

DELETE r
FROM resumes r
JOIN (
    SELECT resume_id
    FROM (
        SELECT
            resume_id,
            ROW_NUMBER() OVER (
                PARTITION BY user_id
                ORDER BY created_at DESC, resume_id DESC
            ) AS rn
        FROM resumes
    ) ranked_resumes
    WHERE rn > 1
) duplicated_resumes
ON r.resume_id = duplicated_resumes.resume_id;

-- =========================================================
-- 2. New tables
-- =========================================================

CREATE TABLE resume_certifications (
                                       resume_certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       resume_id BIGINT NOT NULL,
                                       name VARCHAR(255) NOT NULL,
                                       type VARCHAR(30) NOT NULL,
                                       issuer VARCHAR(255) NOT NULL,
                                       acquired_date DATE NOT NULL,
                                       score_or_grade VARCHAR(100) NULL,
                                       certification_order INT NOT NULL,

                                       CONSTRAINT fk_resume_certifications_resume
                                           FOREIGN KEY (resume_id)
                                               REFERENCES resumes(resume_id)
                                               ON DELETE CASCADE,

                                       INDEX idx_resume_certification_order (resume_id, certification_order)
);

CREATE TABLE qualification_exam_schedules (
                                              qualification_exam_schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              qualification_name VARCHAR(255) NULL,
                                              jm_cd VARCHAR(20) NULL,
                                              impl_year INT NOT NULL,
                                              impl_seq INT NOT NULL,
                                              qualification_type_code VARCHAR(10) NOT NULL,
                                              qualification_type_name VARCHAR(100) NOT NULL,
                                              description VARCHAR(500) NOT NULL,
                                              doc_reg_start_date DATE NULL,
                                              doc_reg_end_date DATE NULL,
                                              doc_exam_start_date DATE NULL,
                                              doc_exam_end_date DATE NULL,
                                              last_synced_at DATETIME NOT NULL,

                                              INDEX idx_qualification_exam_schedule_lookup (
        impl_year,
        qualification_type_code,
        impl_seq
    ),
                                              INDEX idx_qualification_exam_schedule_exam_date (doc_exam_start_date)
);

CREATE TABLE qualification_codes (
                                     qualification_code_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     jm_cd VARCHAR(20) NOT NULL,
                                     qualification_name VARCHAR(255) NOT NULL,
                                     qualification_type_code VARCHAR(10) NOT NULL,
                                     qualification_type_name VARCHAR(100) NOT NULL,
                                     series_code VARCHAR(20) NULL,
                                     series_name VARCHAR(100) NULL,
                                     major_field_code VARCHAR(20) NULL,
                                     major_field_name VARCHAR(100) NULL,
                                     middle_field_code VARCHAR(20) NULL,
                                     middle_field_name VARCHAR(100) NULL,
                                     last_synced_at DATETIME NOT NULL,

                                     UNIQUE KEY uq_qualification_codes_jm_cd (jm_cd),
                                     INDEX idx_qualification_codes_name (qualification_name)
);

CREATE TABLE ai_request_histories (
                                      ai_request_history_id BIGINT NOT NULL AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      feature_type VARCHAR(50) NOT NULL,
                                      status VARCHAR(30) NOT NULL,
                                      request_snapshot_json JSON NULL,
                                      result_json JSON NULL,
                                      error_message TEXT NULL,
                                      created_at DATETIME(6) NOT NULL,
                                      completed_at DATETIME(6) NULL,

                                      PRIMARY KEY (ai_request_history_id),
                                      INDEX idx_ai_request_histories_created_at (created_at),
                                      INDEX idx_ai_request_histories_feature_created_at (feature_type, created_at),
                                      INDEX idx_ai_request_histories_user_feature_created_at (user_id, feature_type, created_at)
);

CREATE TABLE payment_idempotencies (
                                       payment_idempotency_id BIGINT NOT NULL AUTO_INCREMENT,
                                       user_id BIGINT NOT NULL,
                                       idempotency_key VARCHAR(100) NOT NULL,
                                       request_fingerprint VARCHAR(200) NOT NULL,
                                       status VARCHAR(20) NOT NULL,
                                       result_json TEXT NULL,
                                       created_at DATETIME(6) NOT NULL,
                                       updated_at DATETIME(6) NOT NULL,

                                       PRIMARY KEY (payment_idempotency_id),

                                       CONSTRAINT uq_payment_idempotencies_user_key
                                           UNIQUE (user_id, idempotency_key),

                                       CONSTRAINT fk_payment_idempotencies_user
                                           FOREIGN KEY (user_id)
                                               REFERENCES users(user_id),

                                       INDEX idx_payment_idempotencies_created_at (created_at)
);

CREATE TABLE notices (
                         notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(200) NOT NULL,
                         content TEXT NOT NULL,
                         pinned BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at DATETIME(6) NOT NULL
);

CREATE INDEX idx_notices_pinned_created_at
    ON notices (pinned, created_at, notice_id);

CREATE TABLE email_outbox (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              to_email VARCHAR(255) NOT NULL,
                              subject VARCHAR(500) NOT NULL,
                              content TEXT NOT NULL,
                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                                  CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED')),
                              created_at DATETIME NOT NULL,
                              sent_at DATETIME,
                              retry_count INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_email_outbox_status
    ON email_outbox (status);

CREATE TABLE coffee_chats (
                              coffee_chat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              student_id BIGINT NOT NULL,
                              instructor_id BIGINT NOT NULL,
                              course_id BIGINT NOT NULL,
                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              accepted_at DATETIME NULL,
                              left_at DATETIME NULL,
                              active_unique_flag TINYINT AS (
        IF(status IN ('PENDING', 'ACCEPTED'), 1, NULL)
    ) STORED,

                              CONSTRAINT fk_coffee_chats_student
                                  FOREIGN KEY (student_id)
                                      REFERENCES users(user_id),
                              CONSTRAINT fk_coffee_chats_instructor
                                  FOREIGN KEY (instructor_id)
                                      REFERENCES users(user_id),
                              CONSTRAINT fk_coffee_chats_course
                                  FOREIGN KEY (course_id)
                                      REFERENCES courses(course_id),

                              INDEX idx_coffee_chats_student_status (student_id, status),
                              INDEX idx_coffee_chats_instructor_status (instructor_id, status),
                              UNIQUE INDEX uq_active_coffee_chat (
                                  student_id,
                                  instructor_id,
                                  course_id,
                                  active_unique_flag
                                  )
);

CREATE TABLE coffee_chat_messages (
                                      message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      coffee_chat_id BIGINT NOT NULL,
                                      sender_id BIGINT NOT NULL,
                                      content TEXT NOT NULL,
                                      is_read BOOLEAN NOT NULL DEFAULT FALSE,
                                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      CONSTRAINT fk_coffee_chat_messages_chat
                                          FOREIGN KEY (coffee_chat_id)
                                              REFERENCES coffee_chats(coffee_chat_id),
                                      CONSTRAINT fk_coffee_chat_messages_sender
                                          FOREIGN KEY (sender_id)
                                              REFERENCES users(user_id),

                                      INDEX idx_coffee_chat_messages_chat_created (
        coffee_chat_id,
        created_at
    )
);

-- =========================================================
-- 3. Existing table changes
-- =========================================================

ALTER TABLE resumes
    ADD CONSTRAINT uq_resumes_user_id UNIQUE (user_id);

ALTER TABLE courses
    ADD COLUMN reject_reason_category VARCHAR(50) NULL AFTER reject_reason,
    ADD COLUMN reject_detail VARCHAR(500) NULL AFTER reject_reason_category;

ALTER TABLE credit_charge_payments
    ADD COLUMN balance_after BIGINT NULL,
    ADD COLUMN retry_count INT NOT NULL DEFAULT 0,
    ADD COLUMN last_retried_at DATETIME NULL;

CREATE INDEX idx_payments_user_created_id
    ON payments (user_id, created_at, payment_id);

CREATE INDEX idx_order_items_order_type
    ON order_items (order_id, item_type);

CREATE INDEX idx_subscription_payments_user_paid_id
    ON subscription_payments (user_id, paid_at, subscription_payment_id);

CREATE INDEX idx_credit_charge_payments_user_status_approved_id
    ON credit_charge_payments (
                               user_id,
                               status,
                               approved_at,
                               credit_charge_payment_id
        );

CREATE INDEX idx_credit_charge_payments_recovery
    ON credit_charge_payments (status, retry_count, requested_at);

ALTER TABLE subscriptions
    ADD COLUMN grace_period_until DATETIME NULL,
    ADD COLUMN last_renewal_failed_at DATETIME NULL,
    ADD COLUMN renewal_retry_count INT NOT NULL DEFAULT 0;

CREATE INDEX idx_subscriptions_renewal_due
    ON subscriptions (status, auto_renew, next_billing_at);

CREATE INDEX idx_subscriptions_grace_period
    ON subscriptions (status, grace_period_until);

ALTER TABLE users
    ADD COLUMN last_login_at DATETIME NULL;

CREATE INDEX idx_email_verifications_email_purpose_created
    ON email_verifications (target_email, purpose, created_at DESC);

ALTER TABLE email_verifications
    MODIFY purpose ENUM(
    'SIGNUP',
    'PASSWORD_RESET',
    'EMAIL_CHANGE',
    'FIND_ID'
    ) NOT NULL;

CREATE INDEX idx_instructor_profiles_user_id
    ON instructor_profiles (user_id);

ALTER TABLE instructor_profiles
DROP INDEX user_id;

ALTER TABLE instructor_profiles
    ADD COLUMN active_unique_user_id BIGINT AS (
        IF(approval_status IN ('PENDING', 'APPROVED'), user_id, NULL)
    ) STORED;

ALTER TABLE instructor_profiles
    ADD UNIQUE INDEX uq_active_instructor_profile (active_unique_user_id);