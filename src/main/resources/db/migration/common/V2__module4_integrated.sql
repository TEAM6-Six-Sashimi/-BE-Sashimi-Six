-- ============================================================
-- V2 integrated: module 4 database state
-- Replaces V2.1 ~ V2.20 migrations.
-- Requires clean DB reset before applying.
-- ============================================================

-- ============================================================
-- 1. Cart / Order item final state
-- ============================================================

ALTER TABLE cart_items
    ADD INDEX idx_cart_items_user_id (user_id);

ALTER TABLE order_items
    ADD COLUMN item_type VARCHAR(30) NOT NULL DEFAULT 'COURSE',
    ADD COLUMN item_id BIGINT NULL;

UPDATE order_items
SET item_id = course_id
WHERE item_id IS NULL;

ALTER TABLE order_items
DROP FOREIGN KEY fk_order_items_course;

ALTER TABLE order_items
    MODIFY COLUMN item_id BIGINT NOT NULL,
    MODIFY COLUMN course_id BIGINT NULL;


-- ============================================================
-- 2. Credit charge payments
-- ============================================================

CREATE TABLE credit_charge_payments (
                                        credit_charge_payment_id BIGINT NOT NULL AUTO_INCREMENT,
                                        user_id BIGINT NOT NULL,
                                        order_id VARCHAR(64) NOT NULL,
                                        payment_key VARCHAR(200) NULL,
                                        amount BIGINT NOT NULL,
                                        status VARCHAR(30) NOT NULL,
                                        failure_reason VARCHAR(500) NULL,
                                        requested_at DATETIME(6) NOT NULL,
                                        approved_at DATETIME(6) NULL,
                                        payment_method VARCHAR(50) NULL,
                                        PRIMARY KEY (credit_charge_payment_id),
                                        UNIQUE KEY uq_credit_charge_payments_order_id (order_id),
                                        UNIQUE KEY uq_credit_charge_payments_payment_key (payment_key),
                                        KEY idx_credit_charge_payments_user_id (user_id)
);


-- ============================================================
-- 3. NCS
-- ============================================================

CREATE TABLE ncs_info (
                          ncs_info_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          ncs_code VARCHAR(50) NOT NULL UNIQUE,
                          category_path VARCHAR(500),
                          job_name VARCHAR(255),
                          job_description TEXT,
                          ability_unit_code VARCHAR(50),
                          ability_unit_name VARCHAR(255),
                          ability_unit_description TEXT,
                          last_synced_at DATETIME
);

ALTER TABLE categories
    ADD COLUMN ncs_info_id BIGINT NULL;

CREATE INDEX idx_categories_ncs_info_id
    ON categories(ncs_info_id);

ALTER TABLE categories
    ADD CONSTRAINT fk_categories_ncs_info
        FOREIGN KEY (ncs_info_id)
            REFERENCES ncs_info(ncs_info_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;


-- ============================================================
-- 4. Refresh token audit
-- ============================================================

ALTER TABLE refresh_tokens
    ADD COLUMN issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER token,
    ADD COLUMN last_used_at DATETIME NULL AFTER issued_at,
    ADD COLUMN revoked_at DATETIME NULL AFTER expiry_date,
    ADD COLUMN revoked_reason VARCHAR(50) NULL AFTER revoked_at;


-- ============================================================
-- 5. Instructor profile / certification
-- ============================================================

ALTER TABLE instructor_profiles
    ADD COLUMN rejection_category ENUM(
        'INSUFFICIENT_CAREER_PROOF',
        'INSUFFICIENT_BASIC_INFO',
        'UNABLE_TO_VERIFY_IDENTITY',
        'INAPPROPRIATE_CAREER_INCLUDED'
    ) NULL,
    ADD COLUMN rejection_reason VARCHAR(100) NULL,
    ADD COLUMN motivation_letter TEXT NULL,
    ADD COLUMN category_id BIGINT NULL,
    ADD COLUMN profile_image_path VARCHAR(500) NULL,
    ADD COLUMN resume_file_path VARCHAR(500) NULL,
    ADD COLUMN main_careers JSON NULL;

ALTER TABLE instructor_application_certifications
    ADD COLUMN file_path VARCHAR(500) NULL;


-- ============================================================
-- 6. Review reports
-- ============================================================

CREATE TABLE review_reports (
                                report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                review_id BIGINT NOT NULL,
                                reporter_id BIGINT NOT NULL,
                                category VARCHAR(20) NOT NULL,
                                reason VARCHAR(200),
                                status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                created_at DATETIME NOT NULL,
                                CONSTRAINT uq_review_report UNIQUE (review_id, reporter_id),
                                CONSTRAINT fk_review_report_review
                                    FOREIGN KEY (review_id)
                                        REFERENCES reviews(review_id)
);


-- ============================================================
-- 7. User consents
-- ============================================================

ALTER TABLE users
    ADD COLUMN marketing_consent BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN email_consent BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN ai_consent BOOLEAN NOT NULL DEFAULT FALSE;


-- ============================================================
-- 8. Subscription payment final state
-- ============================================================

ALTER TABLE subscriptions
    MODIFY COLUMN type ENUM(
    'BASIC',
    'PREMIUM',
    'PRO',
    'MONTHLY',
    'ANNUAL'
    ) NOT NULL;

UPDATE subscriptions
SET type = 'MONTHLY'
WHERE type IN ('BASIC', 'PREMIUM');

UPDATE subscriptions
SET type = 'ANNUAL'
WHERE type = 'PRO';

ALTER TABLE subscriptions
    MODIFY COLUMN type ENUM('MONTHLY', 'ANNUAL') NOT NULL;

ALTER TABLE subscriptions
    ADD INDEX idx_subscriptions_user_status (
        user_id,
        status
    ),
    ADD INDEX idx_subscriptions_next_billing (
        status,
        auto_renew,
        next_billing_at
    );

CREATE TABLE subscription_payments (
                                       subscription_payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       subscription_id BIGINT NOT NULL,
                                       order_id BIGINT NOT NULL,
                                       payment_id BIGINT NOT NULL,
                                       user_id BIGINT NOT NULL,
                                       order_no VARCHAR(100) NOT NULL,
                                       plan_code ENUM('MONTHLY', 'ANNUAL') NOT NULL,
                                       amount BIGINT NOT NULL,
                                       billing_type ENUM('INITIAL', 'RENEWAL') NOT NULL,
                                       paid_at DATETIME NOT NULL,
                                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_subscription_payments_subscription
                                           FOREIGN KEY (subscription_id)
                                               REFERENCES subscriptions(subscription_id),

                                       CONSTRAINT fk_subscription_payments_order
                                           FOREIGN KEY (order_id)
                                               REFERENCES orders(order_id),

                                       CONSTRAINT fk_subscription_payments_payment
                                           FOREIGN KEY (payment_id)
                                               REFERENCES payments(payment_id),

                                       CONSTRAINT fk_subscription_payments_user
                                           FOREIGN KEY (user_id)
                                               REFERENCES users(user_id),

                                       CONSTRAINT uq_subscription_payments_order
                                           UNIQUE (order_id),

                                       CONSTRAINT uq_subscription_payments_payment
                                           UNIQUE (payment_id),

                                       INDEX idx_subscription_payments_user_paid_at_id (
        user_id,
        paid_at,
        subscription_payment_id
    ),

                                       INDEX idx_subscription_payments_subscription (
        subscription_id
    )
);

ALTER TABLE subscriptions
    ADD COLUMN active_user_id BIGINT
        GENERATED ALWAYS AS (
            CASE
                WHEN status = 'ACTIVE' THEN user_id
                ELSE NULL
                END
            ) STORED;

ALTER TABLE subscriptions
    ADD CONSTRAINT uq_subscriptions_active_user
        UNIQUE (active_user_id);


-- ============================================================
-- 9. Course archived
-- ============================================================

ALTER TABLE courses
    ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;


-- ============================================================
-- 10. AI prompts
-- ============================================================

ALTER TABLE ai_prompts
    MODIFY COLUMN purpose VARCHAR(50) NOT NULL;

INSERT INTO ai_prompts (
    name,
    purpose,
    prompt,
    version,
    is_active
)
SELECT
    '경력 업무 연속성 평가 프롬프트',
    'RESUME_CAREER_CONTINUITY',
    '다음 경력의 직무 흐름을 평가해주세요. 경력 목록: {careerHistory}',
    1,
    TRUE
    WHERE NOT EXISTS (
    SELECT 1
    FROM ai_prompts
    WHERE purpose = 'RESUME_CAREER_CONTINUITY'
      AND version = 1
);

INSERT INTO ai_prompts (
    name,
    purpose,
    prompt,
    version,
    is_active
)
SELECT
    '이력서 보완 피드백 프롬프트',
    'RESUME_IMPROVEMENT',
    '다음 평가 영역에 대한 짧은 보완 문장을 생성해주세요. 평가 영역: {sections}',
    1,
    TRUE
    WHERE NOT EXISTS (
    SELECT 1
    FROM ai_prompts
    WHERE purpose = 'RESUME_IMPROVEMENT'
      AND version = 1
);


-- ============================================================
-- 11. Resume structured form
-- ============================================================

ALTER TABLE resumes
DROP COLUMN title,
    DROP COLUMN content,
    ADD COLUMN entry_level BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE resume_educations (
                                   resume_education_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   resume_id BIGINT NOT NULL,

                                   school_name VARCHAR(255) NOT NULL,
                                   start_year_month CHAR(7) NOT NULL,
                                   end_year_month CHAR(7) NOT NULL,

                                   degree VARCHAR(30) NOT NULL,
                                   major VARCHAR(255) NOT NULL,
                                   graduation_status VARCHAR(30) NOT NULL,
                                   minor_or_research TEXT NULL,

                                   education_order INT NOT NULL,

                                   CONSTRAINT fk_resume_educations_resume
                                       FOREIGN KEY (resume_id)
                                           REFERENCES resumes(resume_id)
                                           ON DELETE CASCADE,

                                   INDEX idx_resume_education_order (
        resume_id,
        education_order
    )
);

CREATE TABLE resume_careers (
                                resume_career_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                resume_id BIGINT NOT NULL,

                                company_name VARCHAR(255) NOT NULL,
                                start_year_month CHAR(7) NOT NULL,
                                end_year_month CHAR(7) NULL,

                                currently_employed BOOLEAN NOT NULL,
                                employment_type VARCHAR(30) NOT NULL,
                                custom_employment_type VARCHAR(255) NULL,
                                job_title VARCHAR(255) NOT NULL,

                                career_order INT NOT NULL,

                                CONSTRAINT fk_resume_careers_resume
                                    FOREIGN KEY (resume_id)
                                        REFERENCES resumes(resume_id)
                                        ON DELETE CASCADE,

                                INDEX idx_resume_career_order (
        resume_id,
        career_order
    )
);


-- ============================================================
-- 12. Drop legacy resume evaluation history
-- ============================================================

DROP TABLE IF EXISTS ai_resume_evaluations;


-- ============================================================
-- 13. Dummy reviews and reports
-- ============================================================

INSERT IGNORE INTO reviews (
    rating,
    content,
    status,
    created_at,
    user_id,
    course_id
)
VALUES
    (5, '정말 유익한 강의였습니다. 강사님 설명이 너무 좋아요.', 'ACTIVE', '2026-05-01 10:00:00', 4, 1),
    (4, '이해하기 쉽게 설명해주셔서 많은 도움이 되었습니다.', 'ACTIVE', '2026-05-02 11:00:00', 4, 3),
    (2, '기대 이하였습니다. 내용이 너무 기초적이었어요.', 'ACTIVE', '2026-05-03 09:00:00', 5, 1),
    (1, '강의 내용이 광고와 달랐습니다. 환불받고 싶어요.', 'ACTIVE', '2026-05-04 14:00:00', 5, 2),
    (5, '최고의 강의입니다. 강력 추천해요.', 'ACTIVE', '2026-05-05 16:00:00', 6, 2),
    (3, '그럭저럭 괜찮은 강의입니다.', 'ACTIVE', '2026-05-06 13:00:00', 6, 4);

INSERT IGNORE INTO review_reports (
    review_id,
    reporter_id,
    category,
    reason,
    status,
    created_at
)
VALUES
    (3, 4, 'SPAM', '광고성 내용이 포함된 것 같습니다.', 'PENDING', '2026-05-10 10:00:00'),
    (4, 6, 'FALSE_INFO', '강의 내용이 사실과 다릅니다.', 'PENDING', '2026-05-11 11:00:00'),
    (6, 4, 'ABUSE', '욕설이 포함되어 있습니다.', 'PENDING', '2026-05-12 09:00:00'),
    (6, 5, 'OTHER', '부적절한 내용이 포함되어 있습니다.', 'PENDING', '2026-05-13 14:00:00');